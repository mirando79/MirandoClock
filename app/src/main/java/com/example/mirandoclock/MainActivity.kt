package com.example.mirandoclock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mirandoclock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            HourlyNotificationScheduler.schedule(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Кнопка "Настройки"
        binding.settingsButton?.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Кнопка "Показать уведомление"
        binding.testNotificationButton.setOnClickListener {
            val intent = Intent(this, HourlyNotificationService::class.java)
            startService(intent)
        }

        // Кнопка "Превью уведомления"
        binding.previewButton.setOnClickListener {
            startActivity(Intent(this, PreviewActivity::class.java))
        }

        // Запрос разрешений для уведомлений (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    HourlyNotificationScheduler.schedule(this)
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            HourlyNotificationScheduler.schedule(this)
        }
    }
}
