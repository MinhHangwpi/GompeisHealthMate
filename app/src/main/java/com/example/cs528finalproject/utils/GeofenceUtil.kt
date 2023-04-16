package com.example.cs528finalproject.utils

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import java.util.concurrent.TimeUnit

class GeofenceUtil {

    companion object {
        fun createGeoFenceList(): List<Geofence> {
            val geoFences = mutableListOf<Geofence>()
            geoFences.add(Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(Constants.MH_HOME)

                // Set the circular region of this geofence.
                .setCircularRegion(
                    42.27459784142134, -71.14521142996533,
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

            geoFences.add(Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(Constants.LOCATION_LIBRARY)

                // Set the circular region of this geofence.
                .setCircularRegion(
                    42.2742, -71.8066,
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

            return geoFences
        }

        fun getGeofencingRequest(geofences: List<Geofence>): GeofencingRequest {
            return GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(geofences)
                .build()
        }
    }
}
