package com.example.cs528finalproject

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.receiver.ActivityTransitionReceiver
import com.example.cs528finalproject.receiver.GeofenceBroadcastReceiver
import com.example.cs528finalproject.utils.ActivityTransitionUtil
import com.example.cs528finalproject.utils.CalorieCalculatorUtil
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.utils.Constants.ACTIVITY_TRANSITION_REQUEST_CODE
import com.example.cs528finalproject.utils.Constants.RC_LOCATION_PERM
import com.example.cs528finalproject.utils.GeofenceUtil
import com.example.cs528finalproject.viewmodels.ActivityState
import com.example.cs528finalproject.viewmodels.GeoFenceState
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.services.LocationService
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

import pub.devrel.easypermissions.AppSettingsDialog
import java.util.*

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks, SensorEventListener {

    // companion object for notification viewPendingIntent
    companion object {
        const val ACTIVITIES_FRAGMENT = 0
        const val FOOD_FRAGMENT = 1
        const val PROFILE_FRAGMENT = 2
        const val SCAN_FRAGMENT = 3
    }

    private lateinit var binding: ActivityMainBinding
    private var mUserDetails: User? = null
    lateinit var geofencingClient: GeofencingClient
    lateinit var activityClient: ActivityRecognitionClient

    // for activity recognition
    private var running: Boolean = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    var currentSteps = 0
    var caloriesBurned = ""
    private var sensorManager: SensorManager? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // share the User object with ViewModel
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val foodLocationsViewModel = ViewModelProvider(this)[FoodLocationsViewModel::class.java]
        val foodMenusViewModel = ViewModelProvider(this)[FoodMenusViewModel::class.java]

        val foodLocations = ArrayList<FoodLocation>()
        foodLocations.add(FoodLocation("starbucks", "Starbucks", 0.0, 20.0))
        foodLocations.add(FoodLocation("goats-head-kitchen", "Goats Head Kitchen", 0.0, 20.0))
        foodLocations.add(FoodLocation("halal-shack", "Halal Shack", 0.0, 20.0))
        foodLocations.add(FoodLocation("morgan-dining-hall", "Morgan Dining Hall", 0.0, 20.0))
        foodLocations.add(FoodLocation("campus-center", "Campus Center", 0.0, 20.0))
        foodLocations.add(FoodLocation("dunkin", "Dunkin", 0.0, 20.0))
        foodLocations.add(FoodLocation("fuller-dining-hall", "Fuller Dining Hall", 0.0, 20.0))
        foodLocations.add(FoodLocation("library-marketplace", "Library Marketplace", 0.0, 20.0))
        foodLocationsViewModel.setFoodLocations(foodLocations)


        val foodMenus = ArrayList<FoodMenu>()
        foodMenusViewModel.setFoodMenus(foodMenus)


        // To log out
        userViewModel.isLoggedIn.observe(this) { logginStatus ->
            if (!logginStatus) {
                FirebaseAuth.getInstance().signOut()
                reload()
            }
        }

        /* Calling the FirestoreClass signInUser function to get the user data from database */
        FireStoreClass().loadUserData(this@MainActivity) { loggedInUser ->
            if (loggedInUser != null) {
                mUserDetails = loggedInUser
//                binding.tvUser.text = "Hello, ${loggedInUser.name}"
                userViewModel.setUser(loggedInUser)
            } else {
                reload()
            }
        }

        val today = Date()
        /* Calling the FirestoreClass signInUser function to get the user data from database */
        FireStoreClass().fetchFoodMenus(this@MainActivity, "halal-shack", today){ foodMenus ->
            if (foodMenus != null) {
                foodMenusViewModel.setFoodMenus(foodMenus)
            }
        }


        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default
        retrieveFragmentIdFromNotificationIntent() // TODO: if the user clicks view when the geofence menu pops up


        FireStoreClass().getMealByUserId { meals ->
            if (meals != null) {
                userViewModel.setMeals(meals)
                // Show ActivitiesFragment by default
                replaceFragment(ActivitiesFragment())
            }
        }

        FireStoreClass().getExerciseByUserId { exercises ->
            if (exercises != null) {
                userViewModel.setExercises(exercises)
                // Show ActivitiesFragment by default
                replaceFragment(ActivitiesFragment())
            }
        }


        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.activities -> replaceFragment(ActivitiesFragment())
                R.id.food -> navigateToFoodFragment(FoodFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.scan -> replaceFragment(ScanFragment())
                else -> {
                }
            }
            true
        }

        // for activity recognition and sensor

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        activityClient = ActivityRecognition.getClient(this)
        // for geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        addGeoFenceListener()

        // Run activity recognition once the app starts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !ActivityTransitionUtil.hasActivityTransitionPermission(this)
        ) {
            //Toast.makeText(this, "No permission found. Requesting permission for Activity Recognition now...", Toast.LENGTH_SHORT).show()
            requestActivityTransitionPermission()
        } else {
            Toast.makeText(
                this,
                "Permission for Activity Recognition found. Start detecting now...",
                Toast.LENGTH_SHORT
            ).show()
            requestForActivityUpdates()
        }

        // update calories whenever use exits an activity, i.e. transitionType == "EXIT"
        ActivityState.getTransitionType().observe(this, Observer { transitionType ->
            var myExercise = userViewModel.selectedUser.value?.let {
                Exercise(
                        id = UUID.randomUUID().toString(),
                userId = it.id,
                timestamp = Date(System.currentTimeMillis()),
                type = ActivityTransitionUtil.toActivityString(ActivityState.getPrevState().value!!),
                duration = ActivityState.getDuration(),
                value = CalorieCalculatorUtil().getCalories(
                    it.weight,
                    Constants.getMetValue(ActivityTransitionUtil.toActivityString(ActivityState.getState().value!!)),
                    ActivityState.getDuration()
                )
                )
            }

            when (transitionType) {

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {

                    Log.d("ACTIVITY TRANSITION", "Transition type: $transitionType")
                    if (myExercise != null) {
                        FireStoreClass().postAnExercise(this, myExercise)
                        userViewModel.addExercise(myExercise)

                        //binding.tvCalories.text = myExercise.value.toString()

                        if (myExercise.type == Constants.WALKING || myExercise.type == Constants.RUNNING) {
                            Log.i("steps", currentSteps.toString())
                            val mySteps =
                                myExercise.copy(type = Constants.STEPS, value = currentSteps)

                            // Post the mySteps object to Firebase
                            FireStoreClass().postAnExercise(this, mySteps)
                            userViewModel.addExercise(mySteps)

                            // need to reset this or will be double counting?
                            currentSteps = 0

                        }
                    }
                    Log.d("ACTIVITY TRANSITION", "The user has exited an activity")
                }


                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> {
                    Log.d("ACTIVITY TRANSITION", "The user has entered an activity")
                }
                else -> {
                    //do nothing
                }
            }
        })


        binding.bgStart.setOnClickListener{
            Log.i("LOCATION","TEST CLICK")
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                startService(this)
            }
        }
        binding.bgStop.setOnClickListener{
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }

    // A PendingIntent for the Broadcast Receiver that handles geofence transitions.
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = "action.ACTION_GEOFENCE_EVENT"
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun addGeoFenceListener() {

        val geoFences = GeofenceUtil.createGeoFenceList()
        val geofencingRequest = GeofenceUtil.getGeofencingRequest(geoFences)
        // First, remove any existing geofences that use our pending intent
        geofencingClient.removeGeofences(geofencePendingIntent)?.run {
            // Regardless of success/failure of the removal, add the new geofence
            addOnCompleteListener {
                // Add the new geofence request with the new geofence
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return@addOnCompleteListener
                }
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.i("GEOFENCE", "ADDED LISTENER")
                    }
                    addOnFailureListener { exception ->
                        Log.e("GEOFENCE", "Error adding geofences: ${exception.message}")
                    }
                }
            }
        }
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user
    }

    private fun reload() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun retrieveFragmentIdFromNotificationIntent() {
        val fragmentId = intent?.getIntExtra("FRAGMENT_ID", FOOD_FRAGMENT)
        when (fragmentId) {
            FOOD_FRAGMENT -> {
                // Display the FoodFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, FoodFragment())
                    .commit()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        Log.d("BottomNav", "moving to fragment $fragment")
        fragmentTransaction.commit()
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d("PERMISSION", "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d("PERMISSION", "onRationaleDenied:$requestCode")
    }

    // sensor and activity transitions
    private fun requestForActivityUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        activityClient
            .requestActivityTransitionUpdates(
                ActivityTransitionUtil.getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Log.d("TAG", "Success - Request Updates")
                ActivityState.startActivityTimer()
            }
            .addOnFailureListener {
                Log.d("TAG", "Failure - Request Updates")
                Toast.makeText(this, "Failure - Request Updates", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deregisterForActivityUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        activityClient
            .removeActivityUpdates(getPendingIntent()) // the same pending intent
            .addOnSuccessListener {
                getPendingIntent().cancel()
                Toast.makeText(
                    this,
                    "Successful deregistration of activity recognition",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Unsuccessful deregistration of activity recognition",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        intent.action = "action.TRANSITIONS_DATA"

        Log.i("intent", intent.data.toString())

        return PendingIntent.getBroadcast(
            this,
            Constants.ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestActivityTransitionPermission() {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        EasyPermissions.requestPermissions(
            this,
            "You need to allow activity transition permissions",
            ACTIVITY_TRANSITION_REQUEST_CODE,
            *perms
        )
    }

    override fun onResume() {
        super.onResume()

        val countSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        val detectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        running = true;

        when {
            detectorSensor != null -> {
                sensorManager?.registerListener(
                    this,
                    detectorSensor,
                    SensorManager.SENSOR_DELAY_UI
                );
            }
            else -> {
                Toast.makeText(this, "Your device is not compatible", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //sensorManager?.unregisterListener(this);
        running = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //var steps = binding.steps;

        if (running) {

            /*totalSteps = event!!.values[0];
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            Log.i("currentSteps", currentSteps.toString())
            binding.tvSteps.text = "$currentSteps steps"
            previousTotalSteps = currentSteps.toFloat()*/

            if (event != null) {
                if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
                    currentSteps++;

//                    Log.i("currentSteps", currentSteps.toString())
//                    binding.tvSteps.text = "$currentSteps steps";
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this);
        deregisterForActivityUpdates()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        requestForActivityUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION", "onRationaleAccepted:$requestCode")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }
    private fun navigateToFoodFragment(fragment:Fragment) {
        val foodLocationsViewModel = ViewModelProvider(this)[FoodLocationsViewModel::class.java]
        foodLocationsViewModel.setSelectedFoodLocation(null)

        replaceFragment(fragment)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
