package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.FoodMenu

class FoodMenusViewModel: ViewModel() {
    private val mutableFoodLocations = MutableLiveData<ArrayList<FoodMenu>>()

    val foodLocations: LiveData<ArrayList<FoodMenu>> get() = mutableFoodLocations

    fun setFoodMenus(foodLocations: ArrayList<FoodMenu>) {
        mutableFoodLocations.value = foodLocations
    }
}