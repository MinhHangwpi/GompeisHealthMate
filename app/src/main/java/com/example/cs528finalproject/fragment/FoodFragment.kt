package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.databinding.FragmentFoodBinding
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    private lateinit var binding: FragmentFoodBinding

    private lateinit var adapter: FoodLocationListViewAdapter

    private var foodLocations : ArrayList<FoodLocation> ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()
        foodLocations = foodLocationsViewModel.foodLocations.value;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(inflater, container, false)
        adapter = context?.let { FoodLocationListViewAdapter(foodLocations!!, it) }!!
        binding.foodLocationList.adapter = adapter

        return binding.root
    }

}