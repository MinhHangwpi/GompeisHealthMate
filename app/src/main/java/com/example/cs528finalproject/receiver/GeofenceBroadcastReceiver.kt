package com.example.cs528finalproject.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentFoodBinding
import com.example.cs528finalproject.fragment.MapsFragment
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.utils.NotificationUtils
import com.example.cs528finalproject.viewmodels.GeoFenceState
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import java.util.ArrayList

class GeofenceBroadcastReceiver: BroadcastReceiver() {
    private val TAG = "GEOFENCE"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "GeofenceBroadcast Receiver received intent: ${intent}")

        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {

            val fenceId = when {
                geofencingEvent?.triggeringGeofences?.isNotEmpty() == true ->
                    geofencingEvent.triggeringGeofences!![0].requestId
                else -> {
                    Log.e(TAG, "No Geofence Trigger Found! Abort mission!")
                    return
                }
            }

//            val foodLocations = arrayOf<String>("starbucks", "goats-head-kitchen", "halal-shack", "morgan-dining-hall", "campus-center", "dunkin", "fuller-dining-hall", "library-marketplace")
            val foodLocations = ArrayList<FoodLocation>()

            foodLocations.add(FoodLocation("starbucks", "Starbucks", 42.273453594863554, -71.80542768371248, 0))
            foodLocations.add(FoodLocation("goats-head-kitchen", "Goats Head Kitchen", 42.27341733075612, -71.80525452049551, 0))
            foodLocations.add(FoodLocation("halal-shack", "Halal Shack", 42.27347550139926, -71.81058822323885, 0))
            foodLocations.add(FoodLocation("morgan-dining-hall", "Morgan Dining Hall", 42.27329722976954, -71.81070758041123, 0))
            foodLocations.add(FoodLocation("campus-center", "Campus Center", 42.27485378466768, -71.80838814915141, 0))
            foodLocations.add(FoodLocation("dunkin", "Dunkin", 42.274932052560985, -71.80825853335277, 0))
            foodLocations.add(FoodLocation("fuller-dining-hall", "Fuller Dining Hall", 42.26638387883931, -71.80917107144728, 0))
            foodLocations.add(FoodLocation("library-marketplace", "Library Marketplace", 42.27415962795238, -71.80642281285824, 0))


//            if (foodLocations.contains(fenceId)) {
            val currentFoodLocation = foodLocations.firstOrNull{it.id == fenceId}
            if (currentFoodLocation != null) {
                if (context != null) {
                    NotificationUtils.showNotification(
                        context,
                        "Food Near you",
                        "You are near ${currentFoodLocation.name}. The following food from the WPIEats menu is recommended for you.",
                        R.drawable.ic_food_notification,
                        101,
                        currentFoodLocation.id
                    )
                }
                Log.d(TAG, "notification sent")

                Toast.makeText(
                    context,
                    "You have been inside the DOWNTOWN Geofence for 10 seconds, incrementing counter",
                    Toast.LENGTH_LONG
                ).show()
                Log.d(
                    TAG,
                    "You have been inside the DOWNTOWN Geofence for 10 seconds, incrementing counter"
                )
                GeoFenceState.incrementHome()
            }
            Log.i("GEOFENCE", fenceId)
//            if (fenceId == Constants.LOCATION_WORCESTER_DOWNTOWN) {
//
//                if (context != null) {
//                    NotificationUtils.showNotification(
//                        context,
//                        "geofence-food notification",
//                        "You are near WPI Campus. The following food from the WPIEats menu is recommended for you.",
//                        R.drawable.ic_food_notification,
//                        20)
//                }
//                Log.d(TAG, "notification sent")
//
//                Toast.makeText(
//                    context,
//                    "You have been inside the DOWNTOWN Geofence for 10 seconds, incrementing counter",
//                    Toast.LENGTH_LONG
//                ).show()
//                Log.d(
//                    TAG,
//                    "You have been inside the DOWNTOWN Geofence for 10 seconds, incrementing counter"
//                )
//                GeoFenceState.incrementHome()
//            } else if(fenceId == Constants.MH_HOME) {
//                Toast.makeText(context, "You have been inside the Home Geofence for 10 seconds, incrementing counter", Toast.LENGTH_LONG).show()
//                Log.d(TAG, "You have been inside the Home Geofence for 10 seconds, incrementing counter")
//                GeoFenceState.incrementHome()
//
//            } else if (fenceId == Constants.LOCATION_LIBRARY) {
//                Toast.makeText(context, "You have been inside the Gordon Library Geofence for 10 seconds, incrementing counter", Toast.LENGTH_LONG).show()
//                Log.d(TAG, "You have been inside the Gordon Library Geofence for 10 seconds, incrementing counter")
//                GeoFenceState.incrementLibrary()
//            }

            Log.i(TAG, "I am dwelling")
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i(TAG, "I enter")
        }
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "I leave")
        }
    }
}