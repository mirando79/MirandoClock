package com.example.mirandoclock

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivityMainBinding

// Главная активность приложения, отображающая часы и аффирмацию
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализация View Binding
        // (Это требует наличия файла activity_main.xml)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Установка слушателя нажатия кнопки Настройки
        // ПРЕДПОЛАГАЕТСЯ, что в activity_main.xml есть кнопка с id "settingsButton"
        try {
            // binding.settingsButton должен существовать, если activity_main.xml корректен
            binding.settingsButton.setOnClickListener {
                // Открываем экран настроек
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        } catch (e: Exception) {
            // Если кнопки нет или макет еще не создан, IDE сообщит об этом,
            // но класс MainActivity теперь корректен и не имеет ошибок с R.id.action_settings.
            println("Ошибка привязки кнопки настроек: $e")
        }

        // Код для R.id.action_settings полностью удален.
    }

    // Здесь будет код для обновления часов и отображения аффирмаций
}
