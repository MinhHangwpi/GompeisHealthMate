package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentFoodBinding
import com.example.cs528finalproject.models.FoodLocation
import com.example.cs528finalproject.utils.NotificationUtils
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.viewmodels.FoodLocationsViewModel
import com.example.cs528finalproject.viewmodels.FoodMenusViewModel
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Use the [FoodFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FoodFragment : Fragment() {
    private lateinit var binding: FragmentFoodBinding
    private var TAG = "Food Fragment"

    private var foodLocations : ArrayList<FoodLocation> ?= null
    private var currentView = "FOOD_LOCATION"; // switch between FOOD_LOCATION and FOOD_MENU screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val foodLocationsViewModel: FoodLocationsViewModel by activityViewModels()
        val foodMenusViewModel: FoodMenusViewModel by activityViewModels()
        foodLocations = foodLocationsViewModel.foodLocations.value;

        foodLocationsViewModel.selectedFoodLocation.observe(this, Observer {
            if (it == null) {
                addFragmentToFragment(FoodLocationList())
                currentView = "FOOD_LOCATION"
            } else {
                val today = Date()

                FireStoreClass().fetchFoodMenus(activity as MainActivity, it.id, today){ foodMenus ->
                    Log.i("FOOD", "food loaded");
                    if (foodMenus != null) {
                        foodMenusViewModel.setFoodMenus(foodMenus)
                    }
                }

                addFragmentToFragment(FoodMenuList())
                currentView = "FOOD_MENU"
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFoodBinding.inflate(inflater, container, false)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        addFragmentToFragment(FoodLocationList())
    }

    private fun addFragmentToFragment(fragment: Fragment){
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(binding.frameFoodAndMenuList.id, fragment).commit()
    }

}