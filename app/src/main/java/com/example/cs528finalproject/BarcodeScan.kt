package com.example.cs528finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        binding.imageButton.setOnClickListener{
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val upc: String? = barcode.rawValue
                    Log.d("BarcodeScan", "Scan success, UPC: $upc")
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