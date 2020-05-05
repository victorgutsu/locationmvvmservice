package io.populi.gpslocationservice

import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.populi.gpslocationservice.notification.AutoPlayNotificationManager


class AutoPlayLocationService : LifecycleService() {
    private val TAG = "GpsLocationService"

    private val viewModel: AutoPlayLocationViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory(application)
            .create(AutoPlayLocationViewModel::class.java)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(
            AutoPlayNotificationManager.AUTOPLAY_NOTIFICATION_ID,
            AutoPlayNotificationManager(this).buildNotification()
        )

        viewModel
            .notificationLiveData
            .observe(this, Observer {
                AutoPlayNotificationManager(this)
                    .buildNotification(AutoPlayNotificationManager.AUTOPLAY_NOTIFICATION_ID, "$it")
            })
        viewModel.start()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}
