package com.example.cs528finalproject.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.viewmodels.GeoFenceState
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
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

            if (fenceId == Constants.MH_HOME) {
                Toast.makeText(context, "You have been inside the Home Geofence for 10 seconds, incrementing counter", Toast.LENGTH_LONG).show()
                Log.d(TAG, "You have been inside the Home Geofence for 10 seconds, incrementing counter")
                GeoFenceState.incrementHome()

            } else if (fenceId == Constants.LOCATION_LIBRARY) {
                Toast.makeText(context, "You have been inside the Gordon Library Geofence for 10 seconds, incrementing counter", Toast.LENGTH_LONG).show()
                Log.d(TAG, "You have been inside the Gordon Library Geofence for 10 seconds, incrementing counter")
                GeoFenceState.incrementLibrary()
            }

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