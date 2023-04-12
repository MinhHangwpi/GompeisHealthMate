package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.databinding.FragmentActivitiesBinding
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.viewmodels.UserViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [Activities.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivitiesFragment : Fragment() {

    private lateinit var binding: FragmentActivitiesBinding
    private lateinit var adapter: MealListViewAdapter
    private var meals = ArrayList<Meal>()
    private var caloriesGained = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel: UserViewModel by activityViewModels()
        meals = userViewModel.meals.value!!
        caloriesGained = userViewModel.getCalGained()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        adapter = context?.let { MealListViewAdapter(meals, it) }!!
        binding.foodListView.adapter = adapter

        binding.calGained.text = "$caloriesGained calories gained"
        return binding.root
    }
}