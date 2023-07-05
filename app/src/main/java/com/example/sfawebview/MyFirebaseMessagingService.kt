package com.example.sfawebview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


const val channelId = "notification_channel"

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle the new device token here
        // You can send the token to your server or perform any necessary operations
        // For testing purposes, you can log the token to verify that it is being retrieved correctly
        Log.d(TAG, "New Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(
                remoteMessage.getNotification()!!.getTitle()!!,
                remoteMessage.getNotification()!!.getBody()!!
            )
        }
    }

    private fun showNotification(title: String, message: String) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            channelId
        )
            .setSmallIcon(R.drawable.sweatfree_logo_round)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(message)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "Sweat Free", NotificationManager.IMPORTANCE_HIGH)
            notificationManager!!.createNotificationChannel(notificationChannel)
        }

        notificationManager!!.notify(0, builder.build())
    }
}
