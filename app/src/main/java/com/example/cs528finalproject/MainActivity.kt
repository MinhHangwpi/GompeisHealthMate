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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.example.cs528finalproject.utils.Constants.RC_LOCATION_PERM
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.receiver.ActivityTransitionReceiver
import com.example.cs528finalproject.receiver.GeofenceBroadcastReceiver
import com.example.cs528finalproject.utils.ActivityTransitionUtil
import com.example.cs528finalproject.utils.CalorieCalculatorUtil
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.utils.Constants.ACTIVITY_TRANSITION_REQUEST_CODE
import com.example.cs528finalproject.utils.GeofenceUtil
import com.example.cs528finalproject.viewmodels.ActivityState
import com.example.cs528finalproject.viewmodels.GeoFenceState
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions

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

        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default
        retrieveFragmentIdFromNotificationIntent() // if the user clicks view when the geofence menu pops up

        if (!hasLocationPermissions()) {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

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
                R.id.food -> replaceFragment(FoodFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.scan -> replaceFragment(ScanFragment())
                else -> {
                }
            }
            true
        }

        // for geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        addGeoFenceListener()
        GeoFenceState.getHomeState().observe(this, Observer { homeVisits ->
            binding.tvHomeGeofence.text = "Home Visits: $homeVisits"
        })

        // for activity recognition and sensor

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        activityClient = ActivityRecognition.getClient(this)

        // Run activity recognition once the app starts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !ActivityTransitionUtil.hasActivityTransitionPermission(this)
        ) {
            Toast.makeText(this, "No permission found. Requesting permission for Activity Recognition now...", Toast.LENGTH_SHORT).show()
            requestActivityTransitionPermission()
        } else {
            Toast.makeText(this, "Permission for Activity Recognition found. Start detecting now...", Toast.LENGTH_SHORT).show()
            requestForActivityUpdates()
        }


        // update calories whenever use exits an activity, i.e. transitionType == "EXIT"
        ActivityState.getTransitionType().observe(this, Observer { transitionType ->
            val myExercise = userViewModel.selectedUser.value?.let {
                Exercise(
                    id = UUID.randomUUID().toString(),
                    userId = it.id,
                    timestamp = Date(System.currentTimeMillis()),
                    type = ActivityTransitionUtil.toActivityString(ActivityState.getState().value!!),
                    duration = ActivityState.getDuration(),
                    value = CalorieCalculatorUtil().getCalories(
                                                    it.weight,
                                                    Constants.getMetValue(ActivityTransitionUtil.toActivityString(ActivityState.getState().value!!)),
                                                    ActivityState.getDuration())
                )
            }
            // For debugging
            binding.tvDetectedTransition.text = "DetectedTransition: ${ActivityTransitionUtil.toTransitionType(transitionType)}"

           when(transitionType) {

                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> {

                    Log.d("ACTIVITY TRANSITION", "Transition type: $transitionType")
                    if (myExercise != null) {
                        FireStoreClass().postAnExercise(this, myExercise)
                        userViewModel.addExercise(myExercise)
                        //binding.tvCalories.text = myExercise.value.toString()

                        if (myExercise.type == Constants.WALKING || myExercise.type == Constants.RUNNING){
                            Log.i("steps", currentSteps.toString())
                            val mySteps = myExercise.copy(type = Constants.STEPS, value = currentSteps)

                            // Post the mySteps object to Firebase
                            FireStoreClass().postAnExercise(this, mySteps)
                            userViewModel.addExercise(mySteps)
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


        // update UI on transition
        // For debugging
        ActivityState.getState().observe(this, Observer { activity ->
            binding.tvDetectedActivity.text = "DetectedActivity: ${ActivityTransitionUtil.toActivityString(activity)}"
        })
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

    private fun hasLocationPermissions(): Boolean {
        return EasyPermissions.hasPermissions(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
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

    private fun requestActivityTransitionPermission() {
        EasyPermissions.requestPermissions(
            this,
            "You need to allow activity transition permissions in order to use this feature.",
            ACTIVITY_TRANSITION_REQUEST_CODE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }

    override fun onResume() {
        super.onResume()
        running = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //var steps = binding.steps;

        if (running) {
            totalSteps = event!!.values[0];
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()

            Log.i("currentSteps", currentSteps.toString())
            binding.tvSteps.text = "$currentSteps steps"
            previousTotalSteps = currentSteps.toFloat()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        deregisterForActivityUpdates()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION", "onPermissionsGranted:" + requestCode + ":" + perms.size)
        requestForActivityUpdates()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("PERMISSION", "onRationaleAccepted:$requestCode")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }
}