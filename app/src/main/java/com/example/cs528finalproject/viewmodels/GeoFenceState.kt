package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData;

object GeoFenceState {
    private val home = MutableLiveData <Int>(0)

    private val library = MutableLiveData <Int>(0)

    fun incrementHome() {
        home.value = home.value?.plus(1)
    }

    fun incrementLibrary() {
        library.value = library.value?.plus(1)
    }

    fun getHomeState(): LiveData<Int> {
        return home
    }

    fun getLibraryState(): LiveData<Int> {
        return library
    }
}
