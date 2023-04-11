package com.example.cs528finalproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.databinding.FragmentFoodBinding
import com.example.cs528finalproject.databinding.FragmentFoodPostBinding
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.models.FoodLocation

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    private var foodLocation: ArrayList<FoodLocation>? = null
    private lateinit var binding: FragmentFoodBinding

    private lateinit var adapter: FoodLocationListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        foodLocation = ArrayList<FoodLocation>()
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(inflater, container, false)


        adapter = context?.let { FoodLocationListViewAdapter(foodLocation!!, it) }!!
        binding.foodLocationList.adapter = adapter

        return binding.root

    }

}