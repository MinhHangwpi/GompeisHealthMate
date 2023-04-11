package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.FoodLocation

class FoodLocationsViewModel: ViewModel() {
    private val mutableFoodLocations = MutableLiveData<ArrayList<FoodLocation>>()
    private val mutableSelectedFoodLocation = MutableLiveData<FoodLocation>()

    val selectedFoodLocation: LiveData<FoodLocation> get() = mutableSelectedFoodLocation
    val foodLocations: LiveData<ArrayList<FoodLocation>> get() = mutableFoodLocations

    fun setFoodLocations(foodLocations: ArrayList<FoodLocation>) {
        mutableFoodLocations.value = foodLocations
    }

    fun addFoodLocation(foodLocation: FoodLocation) {
        mutableFoodLocations.value?.add(foodLocation)
    }
}