package com.example.cs528finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.cs528finalproject.databinding.ActivityBarcodeScanBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class BarcodeScan : AppCompatActivity() {
    private lateinit var binding: ActivityBarcodeScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Google code scanner API
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A)
            .build()
        val scanner = GmsBarcodeScanning.getClient(this, options)

        binding.scanButton.setOnClickListener{
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
                            Log.d("BarcodeScan", "NutritionX API Response: $response")
                            //TODO: Update UI
                        },
                        { error ->
                            Log.d("BarcodeScan", "NutritionX API error: $error")
                            //TODO: Tell user item not found if 404
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
    }
}