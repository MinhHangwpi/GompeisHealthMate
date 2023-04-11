package com.example.cs528finalproject.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.databinding.FragmentFoodPostBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.viewmodels.UserViewModel
import java.util.*

class FoodPostFragment : Fragment() {
    private var _binding: FragmentFoodPostBinding ?= null
    private val binding get() = _binding!!
    private var mUserDetails: User?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get the user info from the ViewModel
        val userViewModel: UserViewModel by activityViewModels()
        mUserDetails = userViewModel.selectedUser.value
        Log.d("ProfileFragment", "received mUserDetails object: ${mUserDetails?.name}")
        mUserDetails?.let{
            setUserDataInUI(it)
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
            userId = mUserDetails?.id!!,
            foodName = binding.etFoodName.text.toString(),
            totalCalories = binding.etTotalCalories.text.toString().toDouble(),
            protein = binding.etProtein.text.toString().toDouble(),
            carbs = binding.etCarbs.text.toString().toDouble(),
            fat = binding.etFat.text.toString().toDouble(),
            fibers = binding.etFibers.text.toString().toDouble(),
            sugar = binding.etSugar.text.toString().toDouble(),
        )
        saveMealToDB(mealObj, requireActivity() as MainActivity)
    }

    private fun setUserDataInUI(user: User){
        mUserDetails = user
        // set user name
        binding.tvUsername.text = "User Name: ${user.name}"
        binding.tvUserid.text = "User ID: ${user.id}"
    }

    private fun saveMealToDB(mealObj: Meal, activity: MainActivity) {
        FireStoreClass().postAMealData(mealObj, activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}