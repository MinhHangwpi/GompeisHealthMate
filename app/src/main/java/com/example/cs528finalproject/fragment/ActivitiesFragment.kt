package com.example.cs528finalproject.fragment

import android.R
import android.util.Log
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.databinding.FragmentActivitiesBinding
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.utils.Constants
import com.example.cs528finalproject.utils.DashboardUtils
import com.example.cs528finalproject.viewmodels.ActivityState
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.android.gms.location.ActivityTransition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ActivitiesFragment : Fragment() {

    private lateinit var binding: FragmentActivitiesBinding
    private lateinit var foodListAdapter: MealListViewAdapter
    private var meals = ArrayList<Meal>()
    private var exercises = ArrayList<Exercise>()
    private var curDate = Calendar.getInstance()
    private lateinit var spinnerDate: String
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
        }
        if (userViewModel.exercises.value != null) {
            exercises =
                DashboardUtils.filterExercisesByDate(userViewModel.exercises.value!!, curDate) as ArrayList<Exercise>
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

        setNutrientProgress()
        setCaloriesProgress()
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

        ActivityState.getTransitionType().observe(viewLifecycleOwner, {transitionType ->
            if (transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT){
                updateDashboardUI(binding.spinner.selectedItem.toString())
                Log.d("UpdateDashboardUI", "Detected Transition: Updating UI for date: ${binding.spinner.selectedItem.toString()}")
            }
        })
    }

    private fun updateDashboardUI(newDate: String) {
        val userViewModel: UserViewModel by activityViewModels()
        val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
        curDate.time = dateFormat.parse(newDate)!!

        meals = DashboardUtils.filterMeals(userViewModel.meals.value!!, curDate) as ArrayList<Meal>
        exercises = DashboardUtils.filterExercisesByDate(userViewModel.exercises.value!!, curDate) as ArrayList<Exercise>
        foodListAdapter.clear()
        foodListAdapter.addAll(meals)
        foodListAdapter.notifyDataSetChanged()

        setNutrientProgress()
        setCaloriesProgress()
    }

    // Sets progress bars and cal gained info
    private fun setNutrientProgress() {
        val caloriesGained = DashboardUtils.getCalGained(meals)
        val nutrientProgress = DashboardUtils.getNutrientProgress(meals)
        Log.d("NutrientProgress", nutrientProgress.toString())

        binding.calGained.text = "$caloriesGained calories gained"
        binding.calGainedProgress.progress = nutrientProgress[Constants.CALORIES]!!
        binding.carbsProgress.progress = nutrientProgress[Constants.CARBS]!!
        binding.fatProgress.progress = nutrientProgress[Constants.FAT]!!
        binding.fibersProgress.progress = nutrientProgress[Constants.FIBER]!!
        binding.proteinProgress.progress = nutrientProgress[Constants.PROTEIN]!!
        binding.sugarProgress.progress = nutrientProgress[Constants.SUGAR]!!

        // update progress text
        binding.carbsProgressText.text = "${nutrientProgress[Constants.CARBS]}/${Constants.DV_CARBS.toInt()}" //TODO: To change the denominator to the value associated with user target
        binding.fatProgressText.text = "${nutrientProgress[Constants.FAT]}/${Constants.DV_FAT.toInt()}"
        binding.fibersProgressText.text = "${nutrientProgress[Constants.CARBS]}/${Constants.DV_FIBER.toInt()}"
        binding.proteinProgressText.text = "${nutrientProgress[Constants.PROTEIN]}/${Constants.DV_PROTEIN.toInt()}"
        binding.sugarProgressText.text = "${nutrientProgress[Constants.SUGAR]}/${Constants.DV_SUGAR.toInt()}"
    }

    // set the total calories burned and calories by activity
    private fun setCaloriesProgress(){
        val caloriesBurned = DashboardUtils.getTotalCaloriesBurned(exercises)
        val burnProgress = DashboardUtils.getCalBurnedByType(exercises)
        val currentSteps = DashboardUtils.getTotalSteps(exercises)

        Log.d("burnProgress", burnProgress.toString() )
        binding.calBurned.text = "$caloriesBurned calories burned"
        binding.calBurnedProgress.progress = caloriesBurned / 2000 // hardcoded assuming the goal is to burn 2000 calories
        binding.tvStill.text = "${burnProgress[Constants.STILL]?.toString() ?: "0"} calories"
        binding.tvWalking.text = "${burnProgress[Constants.WALKING]?.toString() ?: "0"} calories"
        binding.tvRunning.text = "${burnProgress[Constants.RUNNING]?.toString() ?: "0"} calories"
        binding.tvBiking.text = "${burnProgress[Constants.BICYCLING]?.toString() ?: "0"} calories"

        binding.tvSteps.text = "$currentSteps steps"
    }
}