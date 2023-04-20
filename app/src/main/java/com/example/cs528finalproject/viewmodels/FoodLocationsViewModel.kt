package com.example.cs528finalproject.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.FoodLocation
import com.google.android.gms.maps.model.LatLng

class FoodLocationsViewModel: ViewModel() {
    private val mutableFoodLocations = MutableLiveData<ArrayList<FoodLocation>>()
    private val mutableSelectedFoodLocation = MutableLiveData<FoodLocation?>()

    val selectedFoodLocation: LiveData<FoodLocation?> get() = mutableSelectedFoodLocation
    val foodLocations: LiveData<ArrayList<FoodLocation>> get() = mutableFoodLocations

    fun setFoodLocations(foodLocations: ArrayList<FoodLocation>) {
        mutableFoodLocations.value = foodLocations
    }

    fun addFoodLocation(foodLocation: FoodLocation) {
        mutableFoodLocations.value?.add(foodLocation)
    }

    fun setSelectedFoodLocation(foodLocation: FoodLocation?) {
        mutableSelectedFoodLocation.value = foodLocation
    }

    fun updateDistance(currentLocation: LatLng) {
        mutableFoodLocations.value?.forEach {
            val results = FloatArray(3)
            Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, it.latitude, it.longitude, results);
            it.distance = results[0].toInt()
        }
        mutableFoodLocations.value = mutableFoodLocations.value
    }
}