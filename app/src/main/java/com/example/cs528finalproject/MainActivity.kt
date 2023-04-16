package com.example.cs528finalproject

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.example.cs528finalproject.utils.Constants.RC_LOCATION_PERM
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cs528finalproject.fragment.*
import com.example.cs528finalproject.viewmodels.UserViewModel
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    // companion object for notification viewPendingIntent
    companion object {
        const val ACTIVITIES_FRAGMENT = 0
        const val FOOD_FRAGMENT = 1
        const val PROFILE_FRAGMENT = 2
        const val SCAN_FRAGMENT = 3
    }

    private lateinit var binding: ActivityMainBinding
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
//                binding.tvUser.text = "Hello, ${loggedInUser.name}"
                userViewModel.setUser(loggedInUser)
            } else {
                reload()
            }
        }

        replaceFragment(ActivitiesFragment()) // Show ActivitiesFragment by default
        retrieveFragmentIdFromNotificationIntent() // if the user clicks view when the geofence menu pops up

        if(!hasLocationPermissions()){
            // Ask for one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_location),
                RC_LOCATION_PERM,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

        FireStoreClass().getMealByUserId { meals ->
            if (meals != null) {
                userViewModel.setMeals(meals)
                Log.d("MainActivity setMeals", meals.toString())
                // Show ActivitiesFragment by default
                replaceFragment(ActivitiesFragment())
            }
        }

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

    private fun retrieveFragmentIdFromNotificationIntent(){
        val fragmentId = intent?.getIntExtra("FRAGMENT_ID", FOOD_FRAGMENT)
        when (fragmentId) {
            FOOD_FRAGMENT -> {
                // Display the FoodFragment
                supportFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, FoodFragment())
                    .commit()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        Log.d("BottomNav", "moving to fragment $fragment")
        fragmentTransaction.commit()
    }

    private fun hasLocationPermissions():Boolean {
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, "onRationaleAccepted:$requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, "onRationaleDenied:$requestCode")
    }
}