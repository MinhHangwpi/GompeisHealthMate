package com.example.cs528finalproject.models

import com.google.android.gms.maps.model.LatLng

data class LocationModel (
    val location: LatLng? = null,
    var address: String = ""
)
