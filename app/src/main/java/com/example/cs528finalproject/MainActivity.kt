package com.example.cs528finalproject

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.example.cs528finalproject.receiver.ActivityTransitionReceiver
import com.example.cs528finalproject.util.ActivityState
import com.example.cs528finalproject.util.ActivityTransitionUtil
import com.example.cs528finalproject.util.Constants
import com.example.cs528finalproject.util.Constants.ACTIVITY_TRANSITION_REQUEST_CODE
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.DetectedActivity
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, SensorEventListener {

    private var running: Boolean = false;
    private var totalSteps = 0f;
    private var previousTotalSteps = 0f;
    var currentSteps = 0;
    var caloriesBurned = "";
    private var sensorManager: SensorManager? = null;
    private lateinit var binding: ActivityMainBinding;

    private lateinit var client: ActivityRecognitionClient;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        client = ActivityRecognition.getClient(this)

        // Run activity recognition once the app starts
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !ActivityTransitionUtil.hasActivityTransitionPermission(this)
        ) {
            Toast.makeText(this, "No permission found. Requesting permission for Activity Recognition now...", Toast.LENGTH_SHORT).show()
            requestActivityTransitionPermission()
        } else {
            Toast.makeText(this, "Permission for Activity Recognition found. Start detecting now...", Toast.LENGTH_SHORT).show()
            requestForActivityUpdates()
        }

        // update UI on transition
        ActivityState.getState().observe(this, Observer { activity ->
            val (img, text) = when(activity) {
                DetectedActivity.STILL -> R.drawable.still to R.string.still
                DetectedActivity.WALKING -> R.drawable.walking to R.string.walking
                DetectedActivity.RUNNING -> R.drawable.running to R.string.running
                DetectedActivity.ON_BICYCLE -> R.drawable.on_bicycle to R.string.on_bicycle
                else -> R.drawable.in_vehicle to R.string.in_vehicle
            }
            binding.activityImage.setImageResource(img)
            binding.activityText.setText(text)

            Log.i("calories", caloriesBurned)
        })

    }

    private fun requestForActivityUpdates() {
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
                Toast.makeText(this, "Failure - Request Updates", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deregisterForActivityUpdates(){
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
            .removeActivityUpdates(getPendingIntent()) // the same pending intent
            .addOnSuccessListener {
                getPendingIntent().cancel()
                Toast.makeText(this, "Successful deregistration of activity recognition", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Unsuccessful deregistration of activity recognition", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, ActivityTransitionReceiver::class.java)
        intent.action = "action.TRANSITIONS_DATA"

        Log.i("intent", intent.data.toString())

        return PendingIntent.getBroadcast(
            this,
            Constants.ACTIVITY_TRANSITION_REQUEST_CODE_RECEIVER,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun requestActivityTransitionPermission() {
        EasyPermissions.requestPermissions(
            this,
            "You need to allow activity transition permissions in order to use this feature.",
            ACTIVITY_TRANSITION_REQUEST_CODE,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
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

    override fun onSensorChanged(event: SensorEvent?) {
        //var steps = binding.steps;

        if(running){
            totalSteps = event!!.values[0];
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt();

            Log.i("currentSteps", currentSteps.toString())
            binding.steps.text = "$currentSteps steps";
            previousTotalSteps = currentSteps.toFloat();
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        deregisterForActivityUpdates()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        requestForActivityUpdates()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestActivityTransitionPermission()
        }
    }
}