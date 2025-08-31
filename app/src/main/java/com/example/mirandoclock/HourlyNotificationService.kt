package com.example.mirandoclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

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

        // Загружаем layout уведомления
        val views = RemoteViews(packageName, R.layout.notification_hourly)

        // Временно показываем логотип вместо зодиаков
        views.setImageViewResource(R.id.hourImage, R.drawable.ic_logo_ss)

        // Тексты (позже заменим на случайные аффирмации)
        views.setTextViewText(R.id.dailyText, getString(R.string.daily_affirmation))
        views.setTextViewText(R.id.hourlyText, getString(R.string.hourly_affirmation))

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(views)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
