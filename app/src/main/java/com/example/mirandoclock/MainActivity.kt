package com.example.mirandoclock

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mirandoclock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    // Список 12 знаков Зодиака для 12 почасовых настроек
    // Начинаем с Водолея, как в вашем примере в XML
    private val ZODIAC_SIGNS = listOf(
        "Водолей", "Рыбы", "Овен", "Телец", "Близнецы", "Рак",
        "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог"
    )
    private val PREFS_NAME = "MirandoPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Динамическое создание 12 ячеек
        setupHourlySettings()

        // 2. Загрузка и настройка состояния настроек
        loadSettingsState()

        // 3. Настройка слушателей для элементов управления
        setupEventListeners()
    }

    /**
     * Загружает сохраненное состояние из SharedPreferences и применяет его.
     */
    private fun loadSettingsState() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Загрузка состояния ночного режима
        val isNightModeEnabled = sharedPrefs.getBoolean("night_mode_enabled", false)
        binding.nightModeCheckbox.isChecked = isNightModeEnabled
    }

    /**
     * Настраивает слушатели событий для основных элементов настроек.
     */
    private fun setupEventListeners() {
        // Сохранение состояния при изменении чекбокса ночного режима
        binding.nightModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            saveBooleanSetting("night_mode_enabled", isChecked)
            Toast.makeText(this, "Ночной режим: ${if (isChecked) "ВКЛ" else "ВЫКЛ"}", Toast.LENGTH_SHORT).show()
        }

        // Пример заглушки для кнопки "Настройка цвета, шрифта и звука"
        binding.colorFontSoundButton.setOnClickListener {
            Toast.makeText(this, "Открытие экрана кастомизации...", Toast.LENGTH_SHORT).show()
            // TODO: Здесь будет Intent для запуска экрана кастомизации
        }

        // TODO: Добавить логику для dailyAffirmationCheckbox, screenTimeButton и т.д.
    }

    /**
     * Сохраняет булево значение в SharedPreferences.
     */
    private fun saveBooleanSetting(key: String, value: Boolean) {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    /**
     * Динамически генерирует 12 ячеек для почасовых настроек (по знакам Зодиака).
     */
    private fun setupHourlySettings() {
        // Используем темный фон и белый текст из макета activity_settings
        val whiteColor = ContextCompat.getColor(this, android.R.color.white)
        val darkGrayBackground = 0xFF404040.toInt() // #404040

        // Проходим по списку 12 знаков
        ZODIAC_SIGNS.forEachIndexed { index, sign ->
            val hour = (index + 1) % 12
            val displayHour = if (hour == 0) 12 else hour // 0:00 - это 12:00
            val labelText = "$displayHour: $sign"

            // 1. Создание контейнера строки (LinearLayout)
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8.dpToPx() // 8dp
                }
                background = ContextCompat.getDrawable(this@SettingsActivity, R.drawable.rounded_card_dark) // Предполагаем наличие Drawable
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 12.dpToPx())
                setBackgroundColor(darkGrayBackground)
            }

            // 2. Чекбокс
            val checkbox = CheckBox(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                // TODO: Установка tint (нужен AppCompat style)
                buttonTintList = ContextCompat.getColorStateList(this@SettingsActivity, R.color.white)
                // Устанавливаем уникальный ID для сохранения состояния
                id = View.generateViewId()
            }
            rowLayout.addView(checkbox)

            // 3. Текстовое поле (час: знак)
            val label = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = 8.dpToPx()
                }
                text = labelText
                setTextColor(whiteColor)
                textSize = 16f
                id = View.generateViewId()
            }
            rowLayout.addView(label)

            // 4. Спейс (для растяжения)
            val space = Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
                )
            }
            rowLayout.addView(space)

            // 5. Кнопка "Обзор"
            val reviewButton = Button(this).apply {
                text = "Обзор"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    48.dpToPx()
                ).apply {
                    marginEnd = 8.dpToPx()
                }
                textSize = 16f
                setOnClickListener {
                    Toast.makeText(this@SettingsActivity, "Обзор аффирмации для $sign", Toast.LENGTH_SHORT).show()
                }
            }
            rowLayout.addView(reviewButton)

            // 6. Кнопка "Записи"
            val editButton = Button(this).apply {
                text = "Записи"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    48.dpToPx()
                )
                textSize = 16f
                setOnClickListener {
                    Toast.makeText(this@SettingsActivity, "Редактирование аффирмации для $sign", Toast.LENGTH_SHORT).show()
                }
            }
            rowLayout.addView(editButton)

            // Добавление готовой строки в контейнер
            binding.hourlySettingsContainer.addView(rowLayout)

            // Загрузка состояния чекбокса для каждой динамической строки
            // loadHourlyCheckboxState(checkbox, sign) // Логика будет сложнее, оставим на потом
        }
    }

    // Хелпер функция для конвертации dp в px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
