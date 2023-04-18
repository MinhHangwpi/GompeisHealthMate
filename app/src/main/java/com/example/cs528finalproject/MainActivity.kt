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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel
import com.example.cs528finalproject.viewmodels.UserViewModel
import java.util.Date

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
        foodLocations.add(FoodLocation("starbucks", "Starbucks", 0.0, 20.0))
        foodLocations.add(FoodLocation("goats-head-kitchen", "Goats Head Kitchen", 0.0, 20.0))
        foodLocations.add(FoodLocation("halal-shack", "Halal Shack", 0.0, 20.0))
        foodLocations.add(FoodLocation("morgan-dining-hall", "Morgan Dining Hall", 0.0, 20.0))
        foodLocations.add(FoodLocation("campus-center", "Campus Center", 0.0, 20.0))
        foodLocations.add(FoodLocation("dunkin", "Dunkin", 0.0, 20.0))
        foodLocations.add(FoodLocation("fuller-dining-hall", "Fuller Dining Hall", 0.0, 20.0))
        foodLocations.add(FoodLocation("library-marketplace", "Library Marketplace", 0.0, 20.0))
        foodLocationsViewModel.setFoodLocations(foodLocations)


        val foodMenus = ArrayList<FoodMenu>()
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

        val today = Date()
        /* Calling the FirestoreClass signInUser function to get the user data from database */
        FireStoreClass().fetchFoodMenus(this@MainActivity, "halal-shack", today){ foodMenus ->
            if (foodMenus != null) {
                foodMenusViewModel.setFoodMenus(foodMenus)
            }
        }

        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.activities -> replaceFragment(ActivitiesFragment())
                R.id.food -> navigateToFoodFragment(FoodFragment())
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

    private fun navigateToFoodFragment(fragment:Fragment) {
        val foodLocationsViewModel = ViewModelProvider(this)[FoodLocationsViewModel::class.java]
        foodLocationsViewModel.setSelectedFoodLocation(null)

        replaceFragment(fragment)
    }
}