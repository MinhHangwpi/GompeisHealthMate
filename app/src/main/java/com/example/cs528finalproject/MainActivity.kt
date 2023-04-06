package com.example.cs528finalproject

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cs528finalproject.util.ActivityTransitionUtil
import com.google.android.gms.location.ActivityRecognitionClient

class MainActivity : AppCompatActivity(), SensorEventListener {

    private var running: Boolean = false;
    private var totalSteps = 0f;
    private val previousTotalSteps = 0f;
    private var sensorManager: SensorManager? = null;
    private val ACTIVITY_RECOGNITION_REQUEST_CODE: Int = 100;
    private lateinit var client: ActivityRecognitionClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(isPermissionGranted()){
            requestPermission()
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
    }

    override fun onResume() {
        super.onResume()

        val countSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        val detectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        running = true;

        when {
            countSensor != null -> {
                sensorManager?.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            }
            detectorSensor != null -> {
                sensorManager?.registerListener(this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
            }
            else -> {
                Toast.makeText(this, "Your device is not compatible", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this);
        running = false
    }

    private fun requestPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), ACTIVITY_RECOGNITION_REQUEST_CODE)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED;
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            ACTIVITY_RECOGNITION_REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Permission granted
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var steps = findViewById<TextView>(R.id.steps);
        var currentSteps = 0

        if(running){
            totalSteps = event!!.values[0];
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt();

            Log.i("currentSteps", currentSteps.toString())
            steps.text = currentSteps.toString();
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    private fun requestForActivityUpdates(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionUtil.getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                Log.d("TAG", "Success - Request Updates")
                ActivityState.startActivityTimer()
            }
            .addOnFailureListener {
                Log.d("TAG", "Failure - Request Updates")
                Toast.makeText(this, "Failure - Request Updates", Toast.LENGTH_LONG).show()
            }
    }

    private fun getPendingIntent() {

    }
}