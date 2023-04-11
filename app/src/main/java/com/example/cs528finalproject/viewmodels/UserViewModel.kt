package com.example.cs528finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UserViewModel : ViewModel() {
    private val mutableSelectedUser = MutableLiveData<User>()
    private val mutableIsLoggedIn = MutableLiveData<Boolean>()

    val selectedUser: LiveData<User> get() = mutableSelectedUser
    val isLoggedIn: LiveData<Boolean> get() = mutableIsLoggedIn

    fun setUser(user: User){
        mutableSelectedUser.value = user
        mutableIsLoggedIn.value = true
    }

    fun logOut(){
        mutableIsLoggedIn.value = false
    }
}