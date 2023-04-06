package com.example.cs528finalproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.cs528finalproject.databinding.ActivityMockMealBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import java.util.*

class MockMealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMockMealBinding
    private lateinit var mUserDetails: User

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMockMealBinding.inflate(layoutInflater)
        setContentView(binding.root)


        FireStoreClass().loadUserData(this@MockMealActivity)

        binding.btnBack.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnPost.setOnClickListener {
            updateMealInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMealInfo(){

        val mealObj = Meal(
            id = UUID.randomUUID().toString(),
            timestamp = Date(System.currentTimeMillis()),
            userId = mUserDetails.id,
            foodName = binding.etFoodName.text.toString(),
            totalCalories = binding.etTotalCalories.text.toString().toDouble(),
            protein = binding.etProtein.text.toString().toDouble(),
            carbs = binding.etCarbs.text.toString().toDouble(),
            fat = binding.etFat.text.toString().toDouble(),
            fibers = binding.etFibers.text.toString().toDouble(),
            sugar = binding.etSugar.text.toString().toDouble(),
        )

        FireStoreClass().postAMealData(this, mealObj)
    }

    fun setUserDataInUI(user: User){
        mUserDetails = user
        // set user name
        binding.tvUsername.text = "User Name: ${user.name}"
        binding.tvUserid.text = "User ID: ${user.id}"
    }
}