package com.example.cs528finalproject.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.R
import com.example.cs528finalproject.receiver.NotificationReceiver

object NotificationUtils {
    private const val CHANNEL_ID = "my_channel_id"
    private const val CHANNEL_NAME = "My channel"
    private const val CHANNEL_DESCRIPTION = "My Channel Description"

    fun showNotification(context: Context,
                         title: String,
                         message: String,
                         iconResId: Int,
                         notificationId: Int,
                         locationId: String = ""
    ){
        createNotificationChannel(context)

        // Create the pending intent for the "View" button
        val viewIntent = Intent(context, MainActivity::class.java)
            .apply{ action = "VIEW"}
        if (locationId != "") {
            viewIntent.putExtra("FRAGMENT_ID", MainActivity.FOOD_FRAGMENT)
            viewIntent.putExtra("LOCATION_ID", locationId.toString())
            Log.i("NOTIFICATION", viewIntent.extras.toString())
            Log.i("NOTIFICATION", "set location id")
            Log.i("NOTIFICATION", locationId)
        }
        val viewPendingIntent = PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_IMMUTABLE)

        // Create the pending intent for the "Ignore" button
        val ignoreIntent = Intent(context, NotificationReceiver::class.java)
            .apply { action = "IGNORE" }
        val ignorePendingIntent =
            PendingIntent.getBroadcast(context, 0, ignoreIntent, PendingIntent.FLAG_IMMUTABLE)


        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(iconResId)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(viewPendingIntent)
//            .addAction(R.drawable.ic_baseline_check_24, "View", viewPendingIntent)
//            .addAction(R.drawable.ic_baseline_cancel_24, "Ignore", ignorePendingIntent)
            .setTimeoutAfter(60000)
            .setAutoCancel(true)

        // show the notification
        with(NotificationManagerCompat.from(context)){
            // new notification with the same id will overwrite the old one. Thinking of passing an id for the food notifications and a different unique id for the activity notification
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
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
            notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannel(context: Context){
        // Create the NotificationChannel but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply{
                description = CHANNEL_DESCRIPTION
            }
            // Register the channel with the system
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}