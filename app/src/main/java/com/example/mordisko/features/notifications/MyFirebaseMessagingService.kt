package com.example.mordisko.features.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mordisko.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.data["title"]
            ?: remoteMessage.notification?.title
            ?: "Sazón"

        val body = remoteMessage.data["body"]
            ?: remoteMessage.notification?.body
            ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "admin_alerts_channel_v2" // ✅ canal nuevo
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ✅ URI del mp3 en res/raw
        val soundUri: Uri = Uri.parse("android.resource://$packageName/${R.raw.atencion_nuevo_pedido}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Alertas de pedidos (voz)",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notifications)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            // ✅ Para Android < 8, el sonido va aquí
            .setSound(soundUri)
            .build()

        notificationManager.notify(1001, notification)
    }
}






