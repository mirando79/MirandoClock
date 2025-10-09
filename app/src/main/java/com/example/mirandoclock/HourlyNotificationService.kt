package com.example.mirandoclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.*

class HourlyNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showNotification()
        stopSelf()
        return START_NOT_STICKY
    }

    private fun showNotification() {
        val channelId = "hourly_affirmations"
        val channelName = "Hourly Affirmations"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val (title, text) = getAffirmationForCurrentHour()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo) // ← замени на свой логотип в drawable
            .setContentTitle(title)           // всегда "Аффирмация часа"
            .setContentText(text)             // текст аффирмации по часу
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun getAffirmationForCurrentHour(): Pair<String, String> {
        val affirmations = listOf(
            "Ты начинаешь день с ясностью и силой",
            "Ты открыт для новых идей",
            "Ты доверяешь процессу",
            "Ты находишь гармонию внутри себя",
            "Ты излучаешь уверенность",
            "Ты готов к переменам",
            "Ты ощущаешь поддержку мира",
            "Ты действуешь с вдохновением",
            "Ты находишь радость в простом",
            "Ты раскрываешь свой потенциал",
            "Ты спокоен и собран",
            "Ты благодарен за этот момент"
        )
        val hour = Calendar.getInstance().get(Calendar.HOUR) // 0–11
        return Pair(getString(R.string.affirmation_hour), affirmations[hour])
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
