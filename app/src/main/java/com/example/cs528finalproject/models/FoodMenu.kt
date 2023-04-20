package com.example.cs528finalproject.models

data class FoodMenu internal constructor(
    var id: String,
    var name: String,
    var locationId: String,
    var calories: Double,
    var carbs: Double,
    var date: String,
    var fat: Double,
    var protein: Double,
    var sugar: Double,
    var fibers: Double
) {
}
