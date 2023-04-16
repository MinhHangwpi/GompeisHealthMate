package com.example.cs528finalproject.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.util.ActivityState
import com.example.cs528finalproject.util.ActivityTransitionUtil
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionResult
import java.util.*


class ActivityTransitionReceiver: BroadcastReceiver() {

    private var met: Float = 0f;
    private var caloriesBurned: Int = 0;

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent) {



        if (ActivityTransitionResult.hasResult(intent)){
            val result = ActivityTransitionResult.extractResult(intent)
            result?.let{
                result.transitionEvents.forEach { event ->

                    val activityType = ActivityTransitionUtil.toActivityString(event.activityType)
                    val transitionType = ActivityTransitionUtil.toTransitionType(event.transitionType)
                    Log.d("TAG", "Transition: $activityType - $transitionType")

                    // Display toast with old activity
                    if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_EXIT){
                        val duration = (System.currentTimeMillis() - ActivityState.getStartTime()) / 1000
                        val durationMin = duration / 60
                        val durationSec = duration % 60

                        met = when(activityType){
                            "STILL" -> 1.0F
                            "IN_VEHICLE" -> 1.0F
                            "WALKING" -> 2.5F
                            "RUNNING" -> 9.0F
                            "ON_BICYCLE" -> 6.0F
                            else -> 1F
                        }

                        caloriesBurned += ((met * 82 * 3.5 * durationMin)/200).toInt();

                        val i = Intent(context, MainActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        i.putExtra("message", caloriesBurned)
                        context!!.startActivity(i)

                        val info = "You were $activityType for ${durationMin}m, ${durationSec}s and burned $caloriesBurned calories"
                        Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                    }
                    // Update UI with new activity
                    else if(event.transitionType == ActivityTransition.ACTIVITY_TRANSITION_ENTER){
                        ActivityState.startActivityTimer()
                        ActivityState.updateState(event.activityType)
                    }
                }
            }
        }
    }


}