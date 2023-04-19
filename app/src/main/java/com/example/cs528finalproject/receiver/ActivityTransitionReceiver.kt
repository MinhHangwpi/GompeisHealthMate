package com.example.cs528finalproject.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.viewmodels.ActivityState
import com.example.cs528finalproject.utils.ActivityTransitionUtil
import com.example.cs528finalproject.utils.CalorieCalculatorUtil
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import java.util.*


class ActivityTransitionReceiver: BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent) {

        if (ActivityTransitionResult.hasResult(intent)){
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let{
                result.transitionEvents.forEach { event ->

                    val activityType = ActivityTransitionUtil.toActivityString(event.activityType)
                    val transitionType = ActivityTransitionUtil.toTransitionType(event.transitionType)
                    Log.d("ACTIVITY TRANSITION", "Transition: $activityType - $transitionType")

                    // Display toast with old activity
                    if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT){
                        val duration = (System.currentTimeMillis() - ActivityState.getStartTime()) / 1000
                        val durationMin = duration / 60
                        val durationSec = duration % 60

                        val info = "You were $activityType for ${durationMin}m, ${durationSec}s"

                        // TODO: to convert durationSec into float, but currently just use min for now
                        ActivityState.updateDuration(durationMin)
                        ActivityState.updatePrevState()
                        ActivityState.updateTransitionType(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                        Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                        Log.d("ACTIVITY TRANSITION", info)
                    }
                    // Update UI with new activity
                    else if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
                        ActivityState.startActivityTimer()
                        ActivityState.updateState(event.activityType)
                        Log.d("ACTIVITY TRANSITION", "Event object: $event")
                        ActivityState.updateTransitionType(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    }
                }
            }
        }
    }
}