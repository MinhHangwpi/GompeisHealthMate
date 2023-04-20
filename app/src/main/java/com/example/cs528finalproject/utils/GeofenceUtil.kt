package com.example.cs528finalproject.utils

import android.util.Log
import com.example.cs528finalproject.models.FoodLocation
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class GeofenceUtil {

    companion object {
        fun createGeoFenceList(): List<Geofence> {
            val geoFences = mutableListOf<Geofence>()


            val foodLocations = ArrayList<FoodLocation>()
            foodLocations.add(FoodLocation("starbucks", "Starbucks", 42.273453594863554, -71.80542768371248, 0))
            foodLocations.add(FoodLocation("goats-head-kitchen", "Goats Head Kitchen", 42.27341733075612, -71.80525452049551, 0))
            foodLocations.add(FoodLocation("halal-shack", "Halal Shack", 42.27347550139926, -71.81058822323885, 0))
            foodLocations.add(FoodLocation("morgan-dining-hall", "Morgan Dining Hall", 42.27329722976954, -71.81070758041123, 0))
            foodLocations.add(FoodLocation("campus-center", "Campus Center", 42.27485378466768, -71.80838814915141, 0))
            foodLocations.add(FoodLocation("dunkin", "Dunkin", 42.274932052560985, -71.80825853335277, 0))
            foodLocations.add(FoodLocation("fuller-dining-hall", "Fuller Dining Hall", 42.26638387883931, -71.80917107144728, 0))
            foodLocations.add(FoodLocation("library-marketplace", "Library Marketplace", 42.27415962795238, -71.80642281285824, 0))

            foodLocations.forEach {
                geoFences.add(Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(it.id)

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                        it.latitude, it.longitude,
                        50.0F
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(TimeUnit.HOURS.toMillis(1))

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
                    .setLoiteringDelay(3000)

                    // Create the geofence.
                    .build()
                )
            }


//
//            Log.i("GEOFENCE", "setup builder")
//            geoFences.add(Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId(Constants.LOCATION_WORCESTER_DOWNTOWN)
//
//                // Set the circular region of this geofence.
//                .setCircularRegion(
////                    42.2642109, -71.8009096,
//                    42.27329722976954, -71.81070758041123,
//                    20.0F
//                )
//
//                // Set the expiration duration of the geofence. This geofence gets automatically
//                // removed after this period of time.
//                .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
//
//                // Set the transition types of interest. Alerts are only generated for these
//                // transition. We track entry and exit transitions in this sample.
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setLoiteringDelay(3000)
//
//                // Create the geofence.
//                .build()
//            )
//
//            geoFences.add(Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId(Constants.MH_HOME)
//
//                // Set the circular region of this geofence.
//                .setCircularRegion(
//                    42.27421032793396, -71.14535053229274,
//                    50.0F
//                )
//
//                // Set the expiration duration of the geofence. This geofence gets automatically
//                // removed after this period of time.
//                .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
//
//                // Set the transition types of interest. Alerts are only generated for these
//                // transition. We track entry and exit transitions in this sample.
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setLoiteringDelay(10000)
//
//                // Create the geofence.
//                .build()
//            )
//
//            geoFences.add(Geofence.Builder()
//                // Set the request ID of the geofence. This is a string to identify this
//                // geofence.
//                .setRequestId(Constants.LOCATION_LIBRARY)
//
//                // Set the circular region of this geofence.
//                .setCircularRegion(
//                    42.2742, -71.8066,
//                    30.0F
//                )
//
//                // Set the expiration duration of the geofence. This geofence gets automatically
//                // removed after this period of time.
//                .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
//
//                // Set the transition types of interest. Alerts are only generated for these
//                // transition. We track entry and exit transitions in this sample.
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
//                .setLoiteringDelay(10000)
//
//                // Create the geofence.
//                .build()
//            )

            return geoFences
        }

        fun getGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
            return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL)
                .addGeofences(geofences)
                .build()
        }
    }
}
