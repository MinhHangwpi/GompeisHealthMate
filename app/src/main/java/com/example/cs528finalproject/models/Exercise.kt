package com.example.cs528finalproject.models

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import java.util.*

data class Exercise @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String = "",
    val userId: String = "",
    val timestamp: Date = Date(),
    val type: String = "",
    val duration: Long = 0,
    val value: Int = 0 // value represents calories for activity detected by Activity Transition API, but it represents the number of steps if the type is "steps"
) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        Date(parcel.readLong()),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(userId)
        writeLong(timestamp.time)
        writeString(type)
        writeLong(duration)
        writeInt(value)
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}