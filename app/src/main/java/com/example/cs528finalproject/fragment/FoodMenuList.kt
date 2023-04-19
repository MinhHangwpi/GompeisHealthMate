package com.example.cs528finalproject.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentFoodLocationListBinding
import com.example.cs528finalproject.databinding.FragmentFoodMenuListBinding
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel


class FoodMenuList : Fragment() {
    private lateinit var binding: FragmentFoodMenuListBinding

    private lateinit var adapter: FoodMenuListViewAdapter

    private var foodMenu : ArrayList<FoodMenu> ?= null

    private var foodLocation: FoodLocation ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodMenusViewModel: FoodMenusViewModel by activityViewModels()
        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()
        foodMenu = arrayListOf<FoodMenu>();
        foodLocation = foodLocationsViewModel.selectedFoodLocation.value

        foodMenusViewModel.foodLocations.observe(this) {
            foodMenu = it
            adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
            binding.foodMenuList.adapter = adapter

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodMenuListBinding.inflate(inflater, container, false)
        adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
        binding.foodMenuList.adapter = adapter
        binding.txtMenuListLocation.text = foodLocation?.name ?: "Menu"

        binding.foodMenuList.setOnItemClickListener{ parent, view, position, id ->

            Log.d("Food Menu List", "Menu item has been clicked: ${foodMenu?.get(position)}")

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to consume this food?")
                .setPositiveButton("Yes") { _, _ ->
                    // Perform database update here
                    updateMenuItem()
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }

        return binding.root
    }

    private fun updateMenuItem() {

        Log.d("MENU", "Pretending that menu update successful")
//        // Perform database update here
//        // For example, you can use Firebase Realtime Database to update the item
//        val database = Firebase.database.reference
//        val locationId = foodLocation?.id ?: ""
//        val menuItemId = item.id ?: ""
//        database.child("locations").child(locationId).child("menu").child(menuItemId)
//            .setValue(item)
//            .addOnSuccessListener {
//                Toast.makeText(context, "Menu item updated", Toast.LENGTH_SHORT).show()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to update menu item", Toast.LENGTH_SHORT).show()
//            }
    }
}