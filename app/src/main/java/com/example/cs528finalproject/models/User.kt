package com.example.cs528finalproject.models

import android.os.Parcel
import android.os.Parcelable

// Note: this data is made parcelable by installing the parcelable plugin. Make sure you install this plugin to avoid issues.
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val height: Double = 0.0,
    val image: String = "",
    val targetGained: Double = 2000.0,
    val targetBurned: Double = 2000.0,
    val fcmToken: String = "" // to indicate that a specific user is logged in
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!
    ) {

    }
    override fun describeContents() = 0 // why?

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        // need to write* to every field
        writeString(id)
        writeString(name)
        writeString(email)
        writeInt(age)
        writeDouble(weight)
        writeDouble(height)
        writeString(image)
        writeDouble(targetGained)
        writeDouble(targetBurned)
        writeString(fcmToken)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}