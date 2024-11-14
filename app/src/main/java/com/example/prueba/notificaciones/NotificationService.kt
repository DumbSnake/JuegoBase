package com.example.prueba.notificaciones

import android.content.Context
import com.example.prueba.MainActivity
import com.example.prueba.R

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.app.Service
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class NotificationService : Service() {
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(600000L) // 10 minutos
                sendNotification()
            }
        }
    }

    private fun sendNotification() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, "game_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¡Juega Mierda!")
            .setContentText("No te olvides de jugar el mejor juego del mundo.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
