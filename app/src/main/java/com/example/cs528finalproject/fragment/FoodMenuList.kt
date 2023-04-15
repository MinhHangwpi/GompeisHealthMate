package com.example.cs528finalproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodMenusViewModel: FoodMenusViewModel by activityViewModels()
        foodMenu = foodMenusViewModel.foodLocations.value;

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFoodMenuListBinding.inflate(inflater, container, false)
        adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
        binding.foodMenuList.adapter = adapter

        return binding.root
//
//
//        adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
//        binding.foodMenuList.adapter = adapter
//
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_food_menu_list, container, false)
    }
}