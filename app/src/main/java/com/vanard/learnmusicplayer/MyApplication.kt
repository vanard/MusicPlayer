package com.vanard.learnmusicplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.vanard.learnmusicplayer.util.Constants.Companion.CHANNEL_ID_1
import com.vanard.learnmusicplayer.util.Constants.Companion.CHANNEL_ID_2
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        createNotification()
    }

    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(CHANNEL_ID_1,"Channel(1)",
            NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 1 Desc..."

            val channel2 = NotificationChannel(CHANNEL_ID_2,"Channel(2)",
                NotificationManager.IMPORTANCE_HIGH)
            channel1.description = "Channel 2 Desc..."

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel1)
            notificationManager.createNotificationChannel(channel2)
        }
    }


}