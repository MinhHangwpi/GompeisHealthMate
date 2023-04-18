package com.example.cs528finalproject.receiver


import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.cs528finalproject.MainActivity

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Handle the action based on the button that was clicked
        when (intent?.action) {
            "VIEW" -> {
                val viewIntent = Intent(context, MainActivity::class.java)
                intent.putExtra("FRAGMENT_ID", MainActivity.FOOD_FRAGMENT)
                viewIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_CANCEL_CURRENT)
                viewPendingIntent.send()
            }
            "IGNORE" -> {
                // Dismiss the notification
                val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(20)
            }
        }
    }
}