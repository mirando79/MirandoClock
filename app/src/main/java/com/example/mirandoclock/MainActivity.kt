package com.example.mirandoclock

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Создаём канал уведомлений
        val channel = android.app.NotificationChannel(
            "hourly_channel",
            "Ежечасные напоминания",
            android.app.NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.createNotificationChannel(channel)
        // 🕐 Запланировать ежечасное уведомление (бипер)
        scheduleHourlyBeep()

        // 🔘 Кнопка запуска уведомления вручную (для теста)
        binding.testNotificationButton.setOnClickListener {
            val intent = Intent(this, HourlyReceiver::class.java)
            sendBroadcast(intent)
            Toast.makeText(this, "Пробное уведомление отправлено", Toast.LENGTH_SHORT).show()
        }

        // 🔘 Кнопка превью уведомления
        binding.previewButton.setOnClickListener {
            val intent = Intent(this, PreviewActivity::class.java)
            startActivity(intent)
        }

        // ⚙️ Кнопка настроек
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }


    /**
     * Планирует вызов HourlyReceiver каждые 60 минут.
     */
    private fun scheduleHourlyBeep() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, HourlyReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Время первого срабатывания (через 1 минуту — для теста)
        val triggerTime = System.currentTimeMillis() + 10_000L // 10 секунд
        val interval = 60 * 60 * 1000L // 1 час

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            interval,
            pendingIntent
        )

        Toast.makeText(this, "Ежечасное напоминание активировано", Toast.LENGTH_SHORT).show()
    }
}
