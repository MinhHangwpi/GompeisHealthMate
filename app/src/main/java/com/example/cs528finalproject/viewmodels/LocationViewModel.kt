package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.LocationModel
import java.util.ArrayList

class LocationViewModel : ViewModel() {

    private val mutableSelectedItem = MutableLiveData<LocationModel>()
    val selectedItem: LiveData<LocationModel> get() = mutableSelectedItem

    fun selectItem(item: LocationModel){
        mutableSelectedItem.value = item
    }

    companion object {
//        val foodLocations = ArrayList<FoodLocation>()
//        foodLocations.add(FoodLocation("starbucks", "Starbucks", 42.273453594863554, -71.80542768371248, 0))
//        foodLocations.add(FoodLocation("goats-head-kitchen", "Goats Head Kitchen", 42.27341733075612, -71.80525452049551, 0))
//        foodLocations.add(FoodLocation("halal-shack", "Halal Shack", 42.27347550139926, -71.81058822323885, 0))
//        foodLocations.add(FoodLocation("morgan-dining-hall", "Morgan Dining Hall", 42.27329722976954, -71.81070758041123, 0))
//        foodLocations.add(FoodLocation("campus-center", "Campus Center", 42.27485378466768, -71.80838814915141, 0))
//        foodLocations.add(FoodLocation("dunkin", "Dunkin", 42.274932052560985, -71.80825853335277, 0))
//        foodLocations.add(FoodLocation("fuller-dining-hall", "Fuller Dining Hall", 42.26638387883931, -71.80917107144728, 0))
//        foodLocations.add(FoodLocation("library-marketplace", "Library Marketplace", 42.27415962795238, -71.80642281285824, 0))
//        foodLocationsViewModel.setFoodLocations(foodLocations)

    }
}