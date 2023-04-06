package com.example.cs528finalproject.util

import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity


class ActivityTransitionUtil {

    private fun getTransitions(): MutableList<ActivityTransition>{
//         build a list of transitions
        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        return transitions
    }

    fun getActivityTransitionRequest() = ActivityTransitionRequest(getTransitions())

    // helper function to see which state we are in.

        fun toActivityString(activity: Int) : String{
            return when(activity){
                DetectedActivity.STILL -> "STILL"
                DetectedActivity.WALKING -> "WALKING"
                DetectedActivity.IN_VEHICLE -> "IN_VEHICLE"
                DetectedActivity.RUNNING -> "RUNNING"
                else -> "UNKNOWN"
            }
        }

        fun toTransitionType(transitionType: Int): String {
            return when(transitionType){
                ActivityTransition.ACTIVITY_TRANSITION_ENTER -> "Enter"
                ActivityTransition.ACTIVITY_TRANSITION_EXIT -> "Exit"
                else -> "UNKNOWN"
            }
        }

}