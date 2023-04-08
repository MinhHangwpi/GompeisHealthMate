package com.example.cs528finalproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.cs528finalproject.databinding.ActivityBarcodeScanBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.utils.NutritionXRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class BarcodeScan : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeScanBinding
    private val nutrition = mutableMapOf<String, Double>()
    private lateinit var mUserDetails: User
    private lateinit var myMeal: Meal

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // load the user info
        FireStoreClass().loadUserData(this@BarcodeScan)

        // Google code scanner API
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .build()
        val scanner = GmsBarcodeScanning.getClient(this, options)

        binding.confirmButton.setOnClickListener{
            saveMealToDB(myMeal)
            resetUI()
        }

        binding.scanButton.setOnClickListener {
            resetUI()
            // Open google code scanner
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val upc: String? = barcode.rawValue
                    binding.upc.text = "UPC:$upc"
                    Log.d("BarcodeScan", "Scan success, UPC: $upc")

                    // Send request to NutritionX API to get nutrition info for extracted UPC
                    val request = NutritionXRequest(
                        Request.Method.GET,
                        "https://trackapi.nutritionix.com/v2/search/item?upc=$upc",
                        { response ->
                            Log.d("BarcodeScan", "NutritionX API 200 Response: $response")
                            updateUI(response)
                        },
                        { error ->
                            Log.d("BarcodeScan", "NutritionX API error: $error")
                            Toast.makeText(this, "UPC:$upc not found", Toast.LENGTH_SHORT).show()
                        },
                        applicationContext.getString(R.string.nutritionX_APP_ID),
                        applicationContext.getString(R.string.nutritionX_API_KEY)
                    )

                    Volley.newRequestQueue(applicationContext).add(request)
                }
                .addOnCanceledListener {
                    Log.d("BarcodeScan", "Scan canceled")
                }
                .addOnFailureListener { e ->
                    Log.d("BarcodeScan", "Scan error: $e")
                }
        }

        // Update nutrition & calories
        binding.numServings.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
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
                }
                return@OnKeyListener true
            }
            false
        })

        binding.bottomNavigationView.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.activities -> startActivity(
                    Intent(
                        this@BarcodeScan,
                        MockExerciseActivity::class.java
                    )
                )
                R.id.food -> startActivity(Intent(this@BarcodeScan, MockMealActivity::class.java))
                R.id.profile -> startActivity(
                    Intent(
                        this@BarcodeScan,
                        UserProfileActivity::class.java
                    )
                )
                R.id.scan -> startActivity(Intent(this@BarcodeScan, BarcodeScan::class.java))
                else -> {
                }
            }
            true
        }
    }

    // Parses response as JSON to update UI
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(response: String) {
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

            Glide.with(applicationContext)
                .load(img)
                .centerInside()
                .into(binding.foodImage)

            // for firebase update
            myMeal = Meal(
                id = UUID.randomUUID().toString(),
                timestamp = Date(System.currentTimeMillis()),
                userId = mUserDetails.id,
                foodName = foodName,
                totalCalories = nutrition["calories"]!!,
                protein = nutrition["protein"]!!,
                carbs = nutrition["carbs"]!!,
                fat = nutrition["fat"]!!,
                fibers = nutrition["fiber"]!!,
                sugar = nutrition["sugar"]!!
            )

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

    private fun saveMealToDB(mealObj: Meal) {
        FireStoreClass().postAMealData(this, mealObj)
    }

    fun setUserData(user: User) {
        mUserDetails = user
    }
}