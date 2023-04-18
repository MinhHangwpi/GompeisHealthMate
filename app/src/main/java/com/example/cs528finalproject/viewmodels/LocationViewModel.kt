package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.LocationModel

class LocationViewModel : ViewModel() {

    private val mutableSelectedItem = MutableLiveData<LocationModel>()
    val selectedItem: LiveData<LocationModel> get() = mutableSelectedItem

    fun selectItem(item: LocationModel){
        mutableSelectedItem.value = item
    }
}