package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
<<<<<<< HEAD
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser

        if (user == null){
            reload() // if no current user is signed in, direct to the IntroActivity
        }

        /* Calling the FirestoreClass signInUser function to get the user data from database */
        FireStoreClass().loadUserData(this@MainActivity)

        binding.apply{

            btnLogout.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                reload()
            }

            btnProfile.setOnClickListener {
                startActivity(Intent(this@MainActivity, UserProfileActivity::class.java))
            }

            btnExercise.setOnClickListener{
                startActivity(Intent(this@MainActivity, MockExerciseActivity::class.java))
            }

            btnMeal.setOnClickListener{
                startActivity(Intent(this@MainActivity, MockMealActivity::class.java))
            }

            btnAllExercises.setOnClickListener {
                FireStoreClass().getExerciseByUserId()
            }
            btnAllMeals.setOnClickListener {
                FireStoreClass().getMealByUserId()
            }
        }
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user
        // set user name
        binding.tvUser.text = "Hello, ${user.name}"
    }

    private fun reload(){
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
=======
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.cs528finalproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        replaceFragment(Activities());

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.activities -> replaceFragment(Activities())
                R.id.food -> replaceFragment(Food())
                R.id.profile -> replaceFragment(Profile())
                R.id.scan -> replaceFragment(Scan())

                else ->{



                }

            }

            true

        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


>>>>>>> origin/fragment_nav
    }
}