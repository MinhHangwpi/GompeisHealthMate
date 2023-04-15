package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel
import com.example.cs528finalproject.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mUserDetails: User ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // share the User object with ViewModel
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        val foodLocationsViewModel = ViewModelProvider(this)[FoodLocationsViewModel::class.java]
        val foodMenusViewModel = ViewModelProvider(this)[FoodMenusViewModel::class.java]

        val foodLocations = ArrayList<FoodLocation>()
        foodLocations.add(FoodLocation("dunkin", "Dunkin", 0.0, 20.0))
        foodLocations.add(FoodLocation("dunkin2", "Dunkin2", 0.0, 20.0))
        foodLocationsViewModel.setFoodLocations(foodLocations)

        val foodMenus = ArrayList<FoodMenu>()
        foodMenus.add(FoodMenu("food1", "food1", "dunkin", 20.0))
        foodMenus.add(FoodMenu("food2", "food2", "dunkin", 20.0))
        foodMenusViewModel.setFoodMenus(foodMenus)


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
//                binding.tvUser.text = "Hello, ${loggedInUser.name}"
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