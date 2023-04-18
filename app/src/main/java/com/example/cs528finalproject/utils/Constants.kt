package com.example.cs528finalproject.utils

object Constants {
    const val USERS: String = "users"
    const val MEALS: String = "meals"
    const val EXERCISES: String = "exercises"
    const val MENUS: String = "menus"
    const val CHANNEL_ID: String = "your_id"


    const val RC_LOCATION_PERM = 124
    const val ACTIVITY_TRANSITION_REQUEST_CODE = 143
    const val ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER = 166

    const val MH_HOME : String = "MH_HOME_LOCATION"
    const val LOCATION_LIBRARY : String = "LOCATION_LIBRARY"

    const val CALORIES: String = "calories"
    const val CARBS: String = "carbs"
    const val FAT: String = "fat"
    const val FIBER: String = "fiber"
    const val PROTEIN: String = "protein"
    const val SUGAR: String = "sugar"
    const val DV_CAL: Double = 2000.0
    const val DV_CARBS: Double = 275.0
    const val DV_FAT: Double = 78.0
    const val DV_FIBER: Double = 28.0
    const val DV_PROTEIN: Double = 50.0
    const val DV_SUGAR: Double = 50.0

    // for Activity Recognition and Step
    const val STEPS: String = "steps"
    const val RUNNING: String = "running"
    const val WALKING: String = "walking"
    const val BICYCLING: String = "on-bicycle"
    const val INVEHICLE: String = "in-vehicle"
    const val STILL: String = "still"


    // MET CONSTANTS
    const val MET_STILL: Float = 1.0F
    const val MET_IN_VEHICLE: Float = 1.0F
    const val MET_WALKING: Float = 2.5F
    const val MET_RUNNING: Float = 9.0F
    const val MET_ON_BICYCLE: Float = 6.0F
    const val MET_OTHER: Float = 1F

    fun getMetValue(activityType: String): Float {
        return when (activityType) {
            STILL -> MET_STILL
            RUNNING -> MET_RUNNING
            WALKING -> MET_WALKING
            BICYCLING -> MET_ON_BICYCLE
            INVEHICLE -> MET_IN_VEHICLE
            // add other cases for other activity types
            else -> MET_OTHER  // default to still
        }
    }
}