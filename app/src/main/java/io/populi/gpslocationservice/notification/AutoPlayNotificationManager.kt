package io.populi.gpslocationservice.notification

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


class AutoPlayNotificationManager(private val context: Context) {
    private val mNotificationManagerCompat: NotificationManagerCompat?

    companion object {
        val TAG = AutoPlayNotificationManager::class.java.simpleName
        const val CHANNEL_ID = "autoplay.channel"
        const val AUTOPLAY_NOTIFICATION_ID = 1615654
    }

    init {
        mNotificationManagerCompat = NotificationManagerCompat.from(context)
        // Cancel all notifications to handle the case where the Service was killed and restarted by the system.
        mNotificationManagerCompat.cancelAll()
    }

    fun buildNotification(id: Int, message: String?="") {
        mNotificationManagerCompat?.notify(
            id,
            buildNotification(message)
        )
    }

    fun buildNotification(message: String?=null): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_LIGHTS)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_menu_mylocation)
            .setTicker("AutoPlay")
            .setContentTitle("AutoPlay")
            .setContentText(message)
            .setContentInfo("Info")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .build()
    }

    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java
            )
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) { // The user-visible name of the channel.
            // The user-visible description of the channel.
            val mChannel = NotificationChannel(
                CHANNEL_ID,
                "AutoPlay Session",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                // Configure the notification channel.
                description = "That service helps u play the nearest track"
                enableLights(true)
                // Sets the notification light color for notifications posted to this
                // channel, if the device supports this feature.
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)

            }
            notificationManager.createNotificationChannel(mChannel)
            Log.d(TAG, "createChannel: New channel created")
        } else {
            Log.d(TAG, "createChannel: Existing channel reused")
        }
    }
}