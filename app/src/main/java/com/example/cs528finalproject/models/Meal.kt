package com.example.cs528finalproject.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

data class Meal @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String = "",
    val userID: String = "",
    val timestamp: Date = Date(),
    val foodName: String = "",
    val totalCalories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fibers: Double = 0.0,
    val sugar: Double = 0.0
) : Parcelable{
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this (
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Date::class.java.classLoader) as Date,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble()
        )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(userID)
        writeLong(timestamp.time)
        writeString(foodName)
        writeDouble(totalCalories)
        writeDouble(protein)
        writeDouble(carbs)
        writeDouble(fat)
        writeDouble(fibers)
        writeDouble(sugar)
    }

    companion object CREATOR : Parcelable.Creator<Meal> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun createFromParcel(parcel: Parcel): Meal {
            return Meal(parcel)
        }

        override fun newArray(size: Int): Array<Meal?> {
            return arrayOfNulls(size)
        }
    }
}
