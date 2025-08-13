package com.example.mirandoclock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Здесь будет логика для работы с элементами макета настроек.
        // Мы добавим её на следующем шаге.
    }
}