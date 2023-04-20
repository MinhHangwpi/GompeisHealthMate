package com.example.cs528finalproject.utils

import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.Meal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardUtils {
    companion object {
        // Sums values of nutrients from list of meals
        fun getNutrientValues(meals: ArrayList<Meal>): Map<String,Int> {
            return mapOf(
                Constants.CALORIES to meals.sumOf { it.totalCalories }.toInt(),
                Constants.CARBS to meals.sumOf { it.carbs }.toInt(),
                Constants.FAT to meals.sumOf { it.fat }.toInt(),
                Constants.FIBER to meals.sumOf { it.fibers }.toInt(),
                Constants.PROTEIN to meals.sumOf { it.protein }.toInt(),
                Constants.SUGAR to meals.sumOf { it.sugar }.toInt()
            )
        }

        // Calculated % daily value progress of nutrients from list of meals
        fun getNutrientProgress(meals: ArrayList<Meal>, ratio: Double): Map<String,Int> {
            return mapOf(
                Constants.CALORIES to (100 * (meals.sumOf { it.totalCalories } / (ratio * Constants.DV_CAL))).toInt(),
                Constants.CARBS to (100 * (meals.sumOf { it.carbs } / (ratio * Constants.DV_CARBS))).toInt(),
                Constants.FAT to (100 * (meals.sumOf { it.fat } / (ratio * Constants.DV_FAT))).toInt(),
                Constants.FIBER to (100 * (meals.sumOf { it.fibers } / (ratio * Constants.DV_FIBER))).toInt(),
                Constants.PROTEIN to (100 * (meals.sumOf { it.protein } / (ratio * Constants.DV_PROTEIN))).toInt(),
                Constants.SUGAR to (100 * (meals.sumOf { it.sugar } / (ratio * Constants.DV_SUGAR))).toInt()
            )
        }

        // Filters list of all user meals to get ones for particular day
        fun filterMeals(meals: ArrayList<Meal>, curDate: Calendar): List<Meal> {
            return meals.filter { meal ->
                val mealDate = Calendar.getInstance()
                mealDate.time = meal.timestamp
                mealDate.get(Calendar.YEAR) == curDate.get(Calendar.YEAR) &&
                        mealDate.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)
            }
        }

        fun getTotalSteps(exercises: List<Exercise>): Int {
            val stepsExercises = exercises.filter { it.type == "steps" }
            return stepsExercises.sumOf { it.value }
        }

        // TODO: // Sums calorie burned from list of execises

        fun getCalBurnedByType(exercises: ArrayList<Exercise>): Map<String, Int> {
            return exercises
                .filterNot { it.type == "steps" }
                .groupBy { it.type }
                .mapValues { (_, v) -> v.sumOf { it.value } }
        }

        fun getTotalCaloriesBurned(exercises: ArrayList<Exercise>): Int{
            return exercises.filterNot{ it.type == Constants.STEPS }.sumOf { it.value }.toInt()
        }

        // TODO: Filters list of exercises to get a set of exercises for a particular day
        fun filterExercisesByDate(exercises: ArrayList<Exercise>, curDate: Calendar): List<Exercise>{
            return exercises.filter { exercise ->
                val exerciseDate = Calendar.getInstance()
                exerciseDate.time = exercise.timestamp
                exerciseDate.get(Calendar.YEAR) == curDate.get(Calendar.YEAR) &&
                        exerciseDate.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)
            }
        }

        // Gets list of days from past week to use in dropdown
        fun getPastWeek(date: Calendar): List<String> {
            val dates = mutableListOf<String>()
            val dateFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            for (i in 1..7) {
                dates.add(dateFormat.format(date.time))
                date.add(Calendar.DATE, -1)
            }
            return dates
        }
    }
}