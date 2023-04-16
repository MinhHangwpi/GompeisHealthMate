package com.example.cs528finalproject

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.receiver.GeofenceBroadcastReceiver
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.viewmodels.GeoFenceState
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // companion object for notification viewPendingIntent
    companion object {
        const val ACTIVITIES_FRAGMENT = 0
        const val FOOD_FRAGMENT = 1
        const val PROFILE_FRAGMENT = 2
        const val SCAN_FRAGMENT = 3
    }

    private lateinit var binding: ActivityMainBinding
    private var mUserDetails: User ?= null
    lateinit var geofencingClient: GeofencingClient


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
        FireStoreClass().loadUserData(this@MainActivity){ loggedInUser ->
            if (loggedInUser != null){
                mUserDetails = loggedInUser
//                binding.tvUser.text = "Hello, ${loggedInUser.name}"
                userViewModel.setUser(loggedInUser)
            } else {
                reload()
            }
        }

        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default
        retrieveFragmentIdFromNotificationIntent() // if the user clicks view when the geofence menu pops up

        FireStoreClass().getMealByUserId { meals ->
            if (meals != null) {
                userViewModel.setMeals(meals)
                Log.d("MainActivity setMeals", meals.toString())
                // Show ActivitiesFragment by default
                replaceFragment(ActivitiesFragment())
            }
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.activities -> replaceFragment(ActivitiesFragment())
                R.id.food -> replaceFragment(FoodFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.scan -> replaceFragment(ScanFragment())
                else ->{
                }
            }
            true
        }

        // for geofencing
        geofencingClient = LocationServices.getGeofencingClient(this)
        createGeoFenceList()
        GeoFenceState.getHomeState().observe(this, Observer { homeVisits ->
            binding.tvHomeGeofence.text = "Home Visits: $homeVisits"
        })

        GeoFenceState.getLibraryState().observe(this, Observer { libraryVisits ->
            binding.tvLibraryGeofence.text = "Library Visits: $libraryVisits"
        })
    }

    // A PendingIntent for the Broadcast Receiver that handles geofence transitions.
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        intent.action = "action.ACTION_GEOFENCE_EVENT"
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createGeoFenceList() {
        val geoFences = mutableListOf<Geofence>()
        geoFences.add(
            Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(Constants.MH_HOME)

            // Set the circular region of this geofence.
            .setCircularRegion(
                42.27421032793396, -71.14535053229274,
                50.0F
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(TimeUnit.HOURS.toMillis(1))

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(10000)

            // Create the geofence.
            .build()
        )

        geoFences.add(
            Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(Constants.LOCATION_LIBRARY)

            // Set the circular region of this geofence.
            .setCircularRegion(
                42.2742, -71.8066,
//                42.263677, -71.802229,
                30.0F
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(TimeUnit.HOURS.toMillis(1))

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(10000)

            // Create the geofence.
            .build()
        )

        // Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
            // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
            // is already inside that geofence.
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)

            // Add the geofences to be monitored by geofencing service.
            .addGeofences(geoFences)
            .build()

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
//                        Log.e("GEOFENCE", "FAILURE LISTENER...")
                    }
                }
            }
        }
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user
    }

    private fun reload() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun retrieveFragmentIdFromNotificationIntent(){
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
}