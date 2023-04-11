package com.example.cs528finalproject.fragment

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    private var foodLocations : ArrayList<FoodLocation> ?= null
    private var currentView = "FOOD_LOCATION"; // switch between FOOD_LOCATION and FOOD_MENU screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()
        foodLocations = foodLocationsViewModel.foodLocations.value;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodBinding.inflate(inflater, container, false)

        binding.btnReplaceMenuFragment.setOnClickListener { //your implementation goes here
            addFragmentToFragment(FoodMenuList())
            currentView = "FOOD_MENU"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addFragmentToFragment(FoodLocationList())
    }

    private fun addFragmentToFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.frameFoodAndMenuList.id, fragment).commit()
    }


}