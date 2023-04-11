package com.example.cs528finalproject.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< Updated upstream
import android.widget.ArrayAdapter
import android.widget.ListView
=======
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.cs528finalproject.MainActivity
>>>>>>> Stashed changes
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.databinding.FragmentFoodBinding
<<<<<<< Updated upstream
import com.example.cs528finalproject.databinding.FragmentFoodPostBinding
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.models.FoodLocation

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    private var foodLocation: ArrayList<FoodLocation>? = null
    private lateinit var binding: FragmentFoodBinding
=======
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.viewmodels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

//class FoodFragment : Fragment() {
//    private var _binding: FragmentFoodBinding ?= null
//    private val binding get() = _binding!!
//    private var mUserDetails: User?= null
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentFoodBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // get the user info from the ViewModel
//        val userViewModel: UserViewModel by activityViewModels()
//        mUserDetails = userViewModel.selectedUser.value
//        Log.d("ProfileFragment", "received mUserDetails object: ${mUserDetails?.name}")
//        mUserDetails?.let{
//            setUserDataInUI(it)
//        }
//        binding.btnPost.setOnClickListener {
//            updateMealInfo()
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun updateMealInfo(){
//        val mealObj = Meal(
//            id = UUID.randomUUID().toString(),
//            timestamp = Date(System.currentTimeMillis()),
//            userId = mUserDetails?.id!!,
//            foodName = binding.etFoodName.text.toString(),
//            totalCalories = binding.etTotalCalories.text.toString().toDouble(),
//            protein = binding.etProtein.text.toString().toDouble(),
//            carbs = binding.etCarbs.text.toString().toDouble(),
//            fat = binding.etFat.text.toString().toDouble(),
//            fibers = binding.etFibers.text.toString().toDouble(),
//            sugar = binding.etSugar.text.toString().toDouble(),
//        )
//        saveMealToDB(mealObj, requireActivity() as MainActivity)
//    }
//
//    private fun setUserDataInUI(user: User){
//        mUserDetails = user
//        // set user name
//        binding.tvUsername.text = "User Name: ${user.name}"
//        binding.tvUserid.text = "User ID: ${user.id}"
//    }
//
//    private fun saveMealToDB(mealObj: Meal, activity: MainActivity) {
//        FireStoreClass().postAMealData(mealObj, activity)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

class FoodFragment : Fragment() {

    private var _binding: FragmentFoodBinding? = null
    private val binding get() = _binding!!
    private var mUserDetails: User? = null

    private val userViewModel: UserViewModel by activityViewModels()
>>>>>>> Stashed changes

    private lateinit var adapter: FoodLocationListViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

<<<<<<< Updated upstream
        foodLocation = ArrayList<FoodLocation>()
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))
        foodLocation!!.add(FoodLocation("Location 1", 200))
        foodLocation!!.add(FoodLocation("Location 2", 300))
        foodLocation!!.add(FoodLocation("Location 3", 400))
        foodLocation!!.add(FoodLocation("Location 4", 500))
        foodLocation!!.add(FoodLocation("Location 5", 600))
        foodLocation!!.add(FoodLocation("Location 6", 700))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFoodBinding.inflate(inflater, container, false)

=======
        userViewModel.selectedUser.observe(viewLifecycleOwner) { user ->
            mUserDetails = user
            setUserDataInUI(user)
        }

        binding.btnPost.setOnClickListener {
            updateMealInfo()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateMealInfo() {
        mUserDetails?.let { user ->
            val mealObj = Meal(
                id = UUID.randomUUID().toString(),
                timestamp = Date(System.currentTimeMillis()),
                userId = user.id,
                foodName = binding.etFoodName.text.toString(),
                totalCalories = binding.etTotalCalories.text.toString().toDouble(),
                protein = binding.etProtein.text.toString().toDouble(),
                carbs = binding.etCarbs.text.toString().toDouble(),
                fat = binding.etFat.text.toString().toDouble(),
                fibers = binding.etFibers.text.toString().toDouble(),
                sugar = binding.etSugar.text.toString().toDouble(),
            )
            lifecycleScope.launch {
                saveMealToDB(mealObj)
            }
        }
    }

    private fun setUserDataInUI(user: User) {
        binding.tvUsername.text = "User Name: ${user.name}"
        binding.tvUserid.text = "User ID: ${user.id}"
    }

    private suspend fun saveMealToDB(mealObj: Meal) {
        withContext(Dispatchers.IO) {
            FireStoreClass().postAMealData(mealObj, requireActivity() as MainActivity)
        }
    }
>>>>>>> Stashed changes

        adapter = context?.let { FoodLocationListViewAdapter(foodLocation!!, it) }!!
        binding.foodLocationList.adapter = adapter

        return binding.root

    }

}