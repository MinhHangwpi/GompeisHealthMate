package com.example.cs528finalproject.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.cs528finalproject.*
import com.example.cs528finalproject.databinding.FragmentScanBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.utils.NutritionXRequest
import com.example.cs528finalproject.viewmodels.UserViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private val nutrition = mutableMapOf<String, Double>()
    private var mUserDetails: User ?= null
    private lateinit var myMeal: Meal


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(inflater, container, false)
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
            setUserData(it)
        }

        // Google code scanner API
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .build()
        val scanner = GmsBarcodeScanning.getClient(requireContext(), options)

        binding.confirmButton.setOnClickListener {
            confirmMeal()
            saveMealToDB(myMeal, requireActivity() as MainActivity)
            userViewModel.addMeal(myMeal)
            resetUI()
        }

//        binding.scanButton.setOnClickListener {
//            resetUI()
//            // Open google code scanner
//            scanner.startScan()
//                .addOnSuccessListener { barcode ->
//                    val upc: String? = barcode.rawValue
//                    binding.upc.text = "UPC:$upc"
//                    Log.d("BarcodeScan", "Scan success, UPC: $upc")
//
//                    // Send request to NutritionX API to get nutrition info for extracted UPC
//                    val request = context?.let { it ->
//                        NutritionXRequest(
//                            Request.Method.GET,
//                            "https://trackapi.nutritionix.com/v2/search/item?upc=$upc",
//                            { response ->
//                                Log.d("BarcodeScan", "NutritionX API 200 Response: $response")
//                                updateUI(response)
//                            },
//                            { error ->
//                                Log.d("BarcodeScan", "NutritionX API error: $error")
//                                Toast.makeText(
//                                    requireContext(),
//                                    "UPC:$upc not found",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            },
//                            it.getString(R.string.nutritionX_APP_ID),
//                            it.getString(R.string.nutritionX_API_KEY)
//                        )
//                    }
//
//                    Volley.newRequestQueue(context).add(request)
//                }
//                .addOnCanceledListener {
//                    Log.d("BarcodeScan", "Scan canceled")
//                }
//                .addOnFailureListener { e ->
//                    Log.d("BarcodeScan", "Scan error: $e")
//                }
//        }

        resetUI()
        // Open google code scanner
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val upc: String? = barcode.rawValue
                binding.upc.text = "UPC:$upc"
                Log.d("BarcodeScan", "Scan success, UPC: $upc")

                // Send request to NutritionX API to get nutrition info for extracted UPC
                val request = context?.let { it ->
                    NutritionXRequest(
                        Request.Method.GET,
                        "https://trackapi.nutritionix.com/v2/search/item?upc=$upc",
                        { response ->
                            Log.d("BarcodeScan", "NutritionX API 200 Response: $response")
                            updateUI(response)
                        },
                        { error ->
                            Log.d("BarcodeScan", "NutritionX API error: $error")
                            Toast.makeText(
                                requireContext(),
                                "UPC:$upc not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        it.getString(R.string.nutritionX_APP_ID),
                        it.getString(R.string.nutritionX_API_KEY)
                    )
                }

                Volley.newRequestQueue(context).add(request)
            }
            .addOnCanceledListener {
                Log.d("BarcodeScan", "Scan canceled")
            }
            .addOnFailureListener { e ->
                Log.d("BarcodeScan", "Scan error: $e")
            }

        // Update nutrition & calories
        binding.numServings.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
//            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val servings = binding.numServings.text.toString().toDoubleOrNull()
                if (servings != null && nutrition.isNotEmpty()) {
                    binding.totalCal.text = (nutrition["calories"]?.times(servings)).toString()
                    binding.carbs.text =
                        "Carbs:${(nutrition["carbs"]?.times(servings)).toString()}g"
                    binding.fat.text = "Fat:${(nutrition["fat"]?.times(servings)).toString()}g"
                    binding.fiber.text =
                        "Fiber:${(nutrition["fiber"]?.times(servings)).toString()}g"
                    binding.protein.text =
                        "Protein:${(nutrition["protein"]?.times(servings)).toString()}g"
                    binding.sodium.text =
                        "Sodium:${(nutrition["sodium"]?.times(servings)).toString()}mg"
                    binding.sugar.text =
                        "Sugar:${(nutrition["sugar"]?.times(servings)).toString()}g"

                    binding.foodLayout.visibility = View.VISIBLE
                    binding.confirmButton.visibility = View.VISIBLE
                } else {
                    binding.confirmButton.visibility = View.GONE
                }
//                return@OnKeyListener true
//            }
            false
        })
    }

    // Parses response as JSON to update UI
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateUI(response: String) {
        try {
            val json = JSONObject(response).getJSONArray("foods").getJSONObject(0)
            val foodName = json.getString("food_name")
            val img = json.getJSONObject("photo").getString("thumb")

            nutrition["calories"] = json.getDouble("nf_calories")
            nutrition["carbs"] = json.getDouble("nf_total_carbohydrate")
            nutrition["fat"] = json.getDouble("nf_total_fat")
            nutrition["fiber"] = json.getDouble("nf_dietary_fiber")
            nutrition["protein"] = json.getDouble("nf_protein")
            nutrition["sodium"] = json.getDouble("nf_sodium")
            nutrition["sugar"] = json.getDouble("nf_sugars")

            binding.servingsLayout.visibility = View.VISIBLE
            binding.foodName.text = foodName

            Glide.with(this)
                .load(img)
                .centerInside()
                .into(binding.foodImage)



        } catch (e: JSONException) {
            Log.d("BarcodeScan", "JSON Parse error: $e")
        }
    }

    // Resets values for when user confirms/rescans
    private fun resetUI() {
        nutrition.clear()
        // TODO: To clear the myMeal object
        binding.foodName.text = ""
        binding.upc.text = ""
        binding.numServings.text?.clear()
        binding.foodImage.setImageDrawable(null)
        binding.servingsLayout.visibility = View.INVISIBLE
        binding.foodLayout.visibility = View.INVISIBLE
        binding.confirmButton.visibility = View.GONE
    }

    private fun saveMealToDB(mealObj: Meal, activity: MainActivity) {
        FireStoreClass().postAMealData(mealObj, activity)
    }

    private fun setUserData(user: User) {
        mUserDetails = user
    }

    // creates Mymeal values based on final num servings
    private fun confirmMeal(){
        val servings = binding.numServings.text.toString().toDoubleOrNull() ?: 1.0
            // for firebase update
            myMeal = Meal(
                id = UUID.randomUUID().toString(),
                timestamp = Date(System.currentTimeMillis()),
                userId = mUserDetails?.id!!,
                foodName = binding.foodName.text.toString(),
                totalCalories = nutrition["calories"]!! * servings,
                protein = nutrition["protein"]!! * servings,
                carbs = nutrition["carbs"]!! * servings,
                fat = nutrition["fat"]!! * servings,
                fibers = nutrition["fiber"]!! * servings,
                sugar = nutrition["sugar"]!! * servings
            )
    }
}