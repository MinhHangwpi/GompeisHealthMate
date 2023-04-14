package com.example.cs528finalproject.fragment

import android.R
import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.databinding.FragmentActivitiesBinding
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.utils.DashboardUtils
import com.example.cs528finalproject.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [Activities.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActivitiesFragment : Fragment() {

    private lateinit var binding: FragmentActivitiesBinding
    private lateinit var foodListAdapter: MealListViewAdapter
    private var meals = ArrayList<Meal>()
    private var caloriesGained = 0
    private var curDate = Calendar.getInstance()

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val userViewModel: UserViewModel by activityViewModels()
//        meals = DashboardUtils.filterMeals(userViewModel.meals.value!!, curDate) as ArrayList<Meal>
//        caloriesGained = DashboardUtils.getCalGained(meals)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userViewModel: UserViewModel by activityViewModels()
        if (userViewModel.meals.value != null) {
            meals =
                DashboardUtils.filterMeals(userViewModel.meals.value!!, curDate) as ArrayList<Meal>
            caloriesGained = DashboardUtils.getCalGained(meals)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        foodListAdapter = context?.let { MealListViewAdapter(meals, it) }!!
        binding.foodListView.adapter = foodListAdapter

        val spinnerDates = DashboardUtils.getPastWeek(curDate)
        val spinnerAdapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerDates)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter

        binding.calGained.text = "$caloriesGained calories gained"
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val newDate = parent?.getItemAtPosition(position) as String
                Log.d("DateSpinner", "Switched date: $newDate")
                updateDashboardUI(newDate)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("DateSpinner", "No date selected")
            }
        }
    }

    private fun updateDashboardUI(newDate: String) {
        val userViewModel: UserViewModel by activityViewModels()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        curDate.time = dateFormat.parse(newDate)!!

        meals = DashboardUtils.filterMeals(userViewModel.meals.value!!, curDate) as ArrayList<Meal>
        foodListAdapter.clear()
        foodListAdapter.addAll(meals)
        foodListAdapter.notifyDataSetChanged()

        caloriesGained = DashboardUtils.getCalGained(meals)
        binding.calGained.text = "$caloriesGained calories gained"
    }
}