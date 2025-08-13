package com.example.mirandoclock

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import java.util.*

class HourlyNotificationService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showHourlyNotification()
        stopSelf()
        return START_NOT_STICKY
    }

    private fun showHourlyNotification() {
        val prefs = getSharedPreferences("mirando_prefs", Context.MODE_PRIVATE)

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) % 12 + 1

        // Получаем аффирмации из настроек
        val dailyAffirmation = prefs.getString("daily_affirmation", "Аффирмация дня")
        val hourlyAffirmation = prefs.getString("hourly_affirmation_$currentHour", "Аффирмация часа")

        // Цвета текста
        val dailyColor = prefs.getInt("daily_text_color", Color.WHITE)
        val hourlyColor = prefs.getInt("hourly_text_color", Color.LTGRAY)

        // Находим ID картинки для текущего часа
        val imageRes = resources.getIdentifier(
            "zodiac_$currentHour", "drawable", packageName
        )

        val remoteViews = RemoteViews(packageName, R.layout.notification_hourly)
        remoteViews.setTextViewText(R.id.dailyText, dailyAffirmation)
        remoteViews.setTextViewText(R.id.hourlyText, hourlyAffirmation)
        remoteViews.setTextColor(R.id.dailyText, dailyColor)
        remoteViews.setTextColor(R.id.hourlyText, hourlyColor)
        if (imageRes != 0) {
            remoteViews.setImageViewResource(R.id.hourImage, imageRes)
        }

        val channelId = "mirando_hourly_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Ежечасные уведомления",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCustomContentView(remoteViews)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()

        manager.notify(1001, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}