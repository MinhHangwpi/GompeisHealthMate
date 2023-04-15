package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.databinding.FragmentFoodBinding
import com.example.cs528finalproject.databinding.FragmentFoodPostBinding
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.services.NotificationUtils

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    private var foodLocation: ArrayList<FoodLocation>? = null
    private lateinit var binding: FragmentFoodBinding
    private lateinit var adapter: FoodLocationListViewAdapter
    private var TAG = "Food Fragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        adapter = context?.let { FoodLocationListViewAdapter(foodLocation!!, it) }!!
        binding.foodLocationList.adapter = adapter

        // get the child fragment manager for the mapsfragment
        val mapsFragment = MapsFragment()
        childFragmentManager.beginTransaction().add(R.id.maps_fragment_container, mapsFragment).commit()

        binding.btnNoti.setOnClickListener {
            //TODO: trigger the notification here
            NotificationUtils.showNotification(requireContext(),
                            "geofence-food notification",
                            "You are near WPI Campus. The following food from the WPIEats menu is recommended for you.",
                                R.drawable.ic_food_notification,
                                20)
            Log.d(TAG, "notification sent")
        }
        return binding.root
    }
}