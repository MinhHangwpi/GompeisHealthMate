package com.example.cs528finalproject.fragment

import android.os.Bundle
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


/**
 * A simple [Fragment] subclass.
 * Use the [FoodMenuList.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        return binding.root
    }
}