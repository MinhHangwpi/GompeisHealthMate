package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.DetectedActivity

object ActivityState {
    private val state = MutableLiveData<Int>(DetectedActivity.STILL)
    private var startTime: Long = 0
    private var duration: Long = 0
    private var calories: Long = 0
    private val transitionType = MutableLiveData<Int>(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
    private val steps = MutableLiveData<Int>(0)


    fun updateState(newState: Int) {
        state.value = newState
    }

    fun getState(): LiveData<Int> {
        return state
    }

    fun startActivityTimer() {
        startTime = System.currentTimeMillis()
    }

    fun getStartTime(): Long {
        return startTime
    }

    fun updateDuration(d: Long) {
        duration = d
    }

    fun getDuration(): Long {
        return duration
    }

    fun updateCalories(c: Long) {
        calories = c
    }

    fun getCalories(): Long {
        return calories
    }

    fun updateTransitionType(newType: Int) {
        transitionType.value = newType
    }

    fun getTransitionType(): LiveData<Int> {
        return transitionType
    }


    fun incrementStep() {
        steps.value = steps.value?.plus(1)
    }

    fun getSteps() :  LiveData<Int> {
        return steps
    }
}