package com.example.cs528finalproject.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.databinding.FragmentFoodMenuListBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel
import com.example.cs528finalproject.viewmodels.UserViewModel
import java.util.*
import kotlin.collections.ArrayList


class FoodMenuList : Fragment() {
    private lateinit var binding: FragmentFoodMenuListBinding

    private lateinit var adapter: FoodMenuListViewAdapter

    private var foodMenu : ArrayList<FoodMenu> ?= null

    private var foodLocation: FoodLocation ?= null

    private var mUserDetails: User?= null
    private lateinit var myMeal: Meal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val foodMenusViewModel: FoodMenusViewModel by activityViewModels()
        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()
        foodMenu = arrayListOf<FoodMenu>();
        foodLocation = foodLocationsViewModel.selectedFoodLocation.value

        foodMenusViewModel.foodLocations.observe(this) {
            foodMenu = it
            adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
            binding.foodMenuList.adapter = adapter

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodMenuListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        // get the user info from the ViewModel

        val userViewModel: UserViewModel by activityViewModels()
        mUserDetails = userViewModel.selectedUser.value
        Log.d("ProfileFragment", "received mUserDetails object: ${mUserDetails?.name}")
        mUserDetails?.let{
            setUserData(it)
        }

        adapter = context?.let { FoodMenuListViewAdapter(foodMenu!!, it) }!!
        binding.foodMenuList.adapter = adapter
        binding.txtMenuListLocation.text = foodLocation?.name ?: "Menu"

        binding.foodMenuList.setOnItemClickListener{ parent, view, position, id ->

            var selectedItem = foodMenu?.get(position)
            Log.d("Food Menu List", "Menu item has been clicked: ${selectedItem?.name}")

            val builder = AlertDialog.Builder(context)
            builder.setMessage("Do you want to consume this food?")
                .setPositiveButton("Yes") { _, _ ->
                    // Perform database update here
                    myMeal = createMealObj(selectedItem?.name!!,
                                        selectedItem?.calories!!,
                                        selectedItem?.protein!!,
                                        selectedItem?.carbs!!,
                                        selectedItem?.fat!!,
                                        selectedItem?.fiber!!,
                                        selectedItem?.sugar!!)
                    saveMealToDB(myMeal, requireActivity() as MainActivity)
                    userViewModel.addMeal(myMeal)
                }
                .setNegativeButton("No") { _, _ -> }
                .show()
        }
    }



    private fun createMealObj(foodName: String, totalCalories: Double, protein: Double, carbs: Double, fat: Double, fibers: Double, sugar: Double) : Meal {
        // for firebase update
        return Meal(
            id = UUID.randomUUID().toString(),
            timestamp = Date(System.currentTimeMillis()),
            userId = mUserDetails?.id!!,
            foodName = foodName,
            totalCalories = totalCalories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            fibers = fibers,
            sugar = sugar
            )
    }

    private fun setUserData(user: User) {
        mUserDetails = user
    }

    private fun saveMealToDB(mealObj: Meal, activity: MainActivity) {
        FireStoreClass().postAMealData(mealObj, activity)
    }
}