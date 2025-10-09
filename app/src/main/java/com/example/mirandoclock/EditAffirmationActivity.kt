package com.example.mirandoclock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity // <-- ВАЖНОЕ НАСЛЕДОВАНИЕ
import com.example.mirandoclock.databinding.ActivityEditAffirmationBinding

// Класс теперь правильно наследуется от AppCompatActivity
class EditAffirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAffirmationBinding
    private var affirmationKey: String? = null
    // Имя для SharedPreferences
    private val prefsName = "MirandoPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация View Binding
        binding = ActivityEditAffirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Получаем ключ, который говорит нам, что редактировать
        affirmationKey = intent.getStringExtra("AFFIRMATION_KEY")

        if (affirmationKey == null) {
            Toast.makeText(this, "Ошибка: Ключ аффирмации не найден.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // 2. Устанавливаем заголовок
        binding.titleTextView.text = getTitleText(affirmationKey!!)

        // 3. Загружаем сохраненный текст для редактирования
        loadAffirmationText()

        // 4. Настраиваем слушатели кнопок
        setupEventListeners()
    }

    /**
     * Определяет текст заголовка в зависимости от ключа.
     */
    private fun getTitleText(key: String): String {
        return if (key == "Daily") {
            "Редактирование ЕЖЕДНЕВНОЙ аффирмации"
        } else {
            "Редактирование аффирмации для знака: $key"
        }
    }

    /**
     * Загружает сохраненный текст аффирмации из SharedPreferences и помещает его в EditText.
     */
    private fun loadAffirmationText() {
        val sharedPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        // Если текст не найден, используем пустую строку.
        val savedText = sharedPrefs.getString(affirmationKey, "")
        binding.affirmationEditText.setText(savedText)
    }

    /**
     * Настраивает слушатели для кнопок сохранения и отмены.
     */
    private fun setupEventListeners() {
        // Кнопка Сохранить
        binding.saveButton.setOnClickListener {
            saveAffirmationText()
        }

        // Кнопка Отмена
        binding.cancelButton.setOnClickListener {
            Toast.makeText(this, "Изменения отменены.", Toast.LENGTH_SHORT).show()
            finish() // Закрывает текущую активность
        }
    }

    /**
     * Сохраняет текст аффирмации в SharedPreferences под соответствующим ключом.
     */
    private fun saveAffirmationText() {
        val textToSave = binding.affirmationEditText.text.toString().trim()
        val sharedPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // Сохраняем текст по ключу
        sharedPrefs.edit().putString(affirmationKey, textToSave).apply()

        Toast.makeText(this, "Аффирмация сохранена!", Toast.LENGTH_SHORT).show()
        finish() // Закрываем активность после сохранения
    }
}
