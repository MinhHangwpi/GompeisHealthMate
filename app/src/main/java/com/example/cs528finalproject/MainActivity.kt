package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private var mUserDetails: User ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // share the User object with ViewModel
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // To log out
        userViewModel.isLoggedIn.observe(this) { logginStatus ->
            if (!logginStatus) {
                FirebaseAuth.getInstance().signOut()
                reload()
            }
        }

        /* Calling the FirestoreClass signInUser function to get the user data from database */
        FireStoreClass().loadUserData(this@MainActivity){ loggedInUser ->
            if (loggedInUser != null){
                mUserDetails = loggedInUser
                binding.tvUser.text = "Hello, ${loggedInUser.name}"
                userViewModel.setUser(loggedInUser)
            } else {
                reload()
            }
        }

        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.activities -> replaceFragment(ActivitiesFragment())
                R.id.food -> replaceFragment(FoodFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.scan -> replaceFragment(ScanFragment())
                else ->{
                }
            }
            true
        }
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user
        // set user name
        mUserDetails?.let{
            binding.tvUser.text = "Hello, ${user.name}"
        }
    }

    private fun reload() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        Log.d("BottomNav", "moving to fragment $fragment")
        fragmentTransaction.commit()
    }
}