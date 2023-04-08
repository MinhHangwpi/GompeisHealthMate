package com.example.cs528finalproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.cs528finalproject.databinding.ActivityMockExerciseBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.User
import java.util.*
import kotlin.collections.HashMap

class MockExerciseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMockExerciseBinding
    private lateinit var mUserDetails: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FireStoreClass().loadUserData(this@MockExerciseActivity)

//        binding.btnBack.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
        binding.btnPost.setOnClickListener {
            updateExerciseInfo()
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            when (it.itemId){
                R.id.activities -> startActivity(Intent(this@MockExerciseActivity, MockExerciseActivity::class.java))
                R.id.food -> startActivity(Intent(this@MockExerciseActivity, MockMealActivity::class.java))
                R.id.profile -> startActivity(Intent(this@MockExerciseActivity, UserProfileActivity::class.java))
                R.id.scan -> startActivity(Intent(this@MockExerciseActivity, BarcodeScan::class.java))
                else -> {
                }
            }
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateExerciseInfo() {
        val exerciseObj = Exercise(
            id = UUID.randomUUID().toString(),
            timestamp = Date(System.currentTimeMillis()),
            userId = mUserDetails.id,
            type = binding.etType.text.toString(),
            value = binding.etValue.text.toString().toDouble()
        )
        FireStoreClass().postAnExercise(this, exerciseObj)
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user
        // set user name
        binding.tvUsername.text = "User Name: ${user.name}"
        binding.tvUserid.text = "User ID: ${user.id}"
    }

    fun exerciseUpdateSuccess(){
        // TODO:
    }
}