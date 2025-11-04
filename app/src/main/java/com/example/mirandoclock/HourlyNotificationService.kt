package com.example.mirandoclock

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import java.util.Calendar

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

        val views = RemoteViews(packageName, R.layout.notification_hourly)
        views.setImageViewResource(R.id.hourImage, R.drawable.ic_logo_ss)
        views.setTextViewText(R.id.dailyText, getString(R.string.affirmation_day))
        views.setTextViewText(R.id.hourlyText, getAffirmationForHour())

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_ss)
            .setCustomContentView(views)
            .setContentTitle(getString(R.string.affirmation_hour_title))
            .setContentText(getAffirmationForHour())
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun getAffirmationForHour(): String {
        val affirmations = listOf(
            "Водолей — думай нестандартно, вдохновляй перемены",
            "Рыбы — доверься течению жизни и своей интуиции",
            "Овен — начни день с энергии и уверенности",
            "Телец — сохраняй спокойствие и внутренний баланс",
            "Близнецы — делись мыслями свободно и легко",
            "Рак — слушай своё сердце, оно знает путь",
            "Лев — сияй и вдохновляй других",
            "Дева — порядок приносит гармонию",
            "Весы — найди красоту в мелочах",
            "Скорпион — действуй решительно",
            "Стрелец — будь открытым для новых идей",
            "Козерог — стойкость приведёт к успеху"
        )

        val hour = Calendar.getInstance().get(Calendar.HOUR) % 12
        return affirmations[hour]
    }


    override fun onBind(intent: Intent?): IBinder? = null
}
