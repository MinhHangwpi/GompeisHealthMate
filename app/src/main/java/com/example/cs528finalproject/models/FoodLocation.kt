package com.example.cs528finalproject.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.util.*

data class FoodLocation internal constructor(var id: String, var name: String, var latitude: Double, var longitude: Double, var distance: Int) {
}
