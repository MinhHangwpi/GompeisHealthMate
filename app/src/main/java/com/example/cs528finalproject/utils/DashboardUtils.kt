package com.example.cs528finalproject.utils

import com.example.cs528finalproject.models.Meal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DashboardUtils {
    companion object {
        // Sums calorie intake from list of meals
        fun getCalGained(meals: ArrayList<Meal>): Int {
            return meals.sumOf { it.totalCalories }.toInt()
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