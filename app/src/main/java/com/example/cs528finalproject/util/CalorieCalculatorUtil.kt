package com.example.cs528finalproject.util

object CalorieCalculatorUtil {

    //Number of steps * 0.63

    //(Weight * MET * 3.5 * / 200) * Time(Minutes)
    fun getCalories(weight: Int, met: Float, duration: Long): Int {
        return (((weight * met * 3.5)/200) * duration).toInt();
    }
}