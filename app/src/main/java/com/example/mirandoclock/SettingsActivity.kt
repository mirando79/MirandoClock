package com.example.mirandoclock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
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

    // Список знаков Зодиака (с 1 по 12 час)
    private val zodiacSigns = listOf(
        "Водолей", "Рыбы", "Овен", "Телец", "Близнецы", "Рак",
        "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог"
    )

    private val prefsName = "MirandoPrefs"

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
        val sharedPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // Загрузка состояния ночного режима
        val isNightModeEnabled = sharedPrefs.getBoolean("night_mode_enabled", false)
        binding.nightModeCheckbox.isChecked = isNightModeEnabled

        // Загрузка состояния ежедневной аффирмации
        val isDailyAffirmationEnabled = sharedPrefs.getBoolean("daily_affirmation_enabled", false)
        binding.dailyAffirmationCheckbox.isChecked = isDailyAffirmationEnabled

        // Загрузка текста ежедневной аффирмации
        // 'Daily' — это ключ для SharedPreferences
        val dailyText = sharedPrefs.getString("Daily", "Аффирмация дня (по умолчанию)")
        binding.dailyAffirmationTextView.text = dailyText
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

        // Сохранение состояния при изменении чекбокса ежедневной аффирмации
        binding.dailyAffirmationCheckbox.setOnCheckedChangeListener { _, isChecked ->
            saveBooleanSetting("daily_affirmation_enabled", isChecked)
            Toast.makeText(this, "Аффирмация дня: ${if (isChecked) "ВКЛ" else "ВЫКЛ"}", Toast.LENGTH_SHORT).show()
        }

        // Кнопка "Записи" для ежедневной аффирмации - запускает EditAffirmationActivity
        binding.dailyAffirmationEditButton.setOnClickListener {
            startEditAffirmationActivity("Daily")
        }

        // Кнопка "Обзор" для ежедневной аффирмации - показывает текущий текст
        binding.dailyImageButton.setOnClickListener {
            showAffirmationReview("Daily")
        }

        // Кнопка "Настройка цвета, шрифта и звука"
        binding.colorFontSoundButton.setOnClickListener {
            Toast.makeText(this, "Открытие экрана кастомизации (WIP)", Toast.LENGTH_SHORT).show()
            // TODO: Здесь будет Intent для запуска экрана кастомизации
        }
    }

    /**
     * Показывает текст аффирмации в Toast для обзора.
     */
    private fun showAffirmationReview(key: String) {
        val sharedPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val text = sharedPrefs.getString(key, "Текст не установлен.")
        Toast.makeText(this, "Обзор [$key]: $text", Toast.LENGTH_LONG).show()
    }

    /**
     * Сохраняет булево значение в SharedPreferences.
     */
    private fun saveBooleanSetting(key: String, value: Boolean) {
        val sharedPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    /**
     * Динамически генерирует 12 ячеек для почасовых настроек (по знакам Зодиака).
     */
    private fun setupHourlySettings() {
        val whiteColor = ContextCompat.getColor(this, android.R.color.white)

        zodiacSigns.forEachIndexed { index, sign ->
            val hour = (index + 1) % 12
            val displayHour = if (hour == 0) 12 else hour
            val labelText = "$displayHour: $sign"
            // Используем имя знака в качестве ключа (напр., "Водолей" или "Телец")
            val signKey = sign.replace(" ", "_")

            // 1. Создание контейнера строки (LinearLayout)
            val rowLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 8.dpToPx()
                }

                background = ContextCompat.getDrawable(this@SettingsActivity, R.drawable.rounded_card_dark)
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 12.dpToPx())
            }

            // 2. Чекбокс
            val checkbox = CheckBox(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                buttonTintList = ContextCompat.getColorStateList(this@SettingsActivity, R.color.white)
                // TODO: Здесь должна быть загрузка и сохранение состояния чекбокса
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
                    showAffirmationReview(signKey)
                }
            }
            rowLayout.addView(reviewButton)

            // 6. Кнопка "Записи" (редактирование)
            val editButton = Button(this).apply {
                text = "Записи"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    48.dpToPx()
                )
                textSize = 16f
                setOnClickListener {
                    // Запускаем экран редактирования с ключом-знаком Зодиака
                    startEditAffirmationActivity(signKey)
                }
            }
            rowLayout.addView(editButton)

            // Добавление готовой строки в контейнер
            binding.hourlySettingsContainer.addView(rowLayout)
        }
    }

    /**
     * Запускает активность редактирования аффирмации, передавая ключ (знак Зодиака или "Daily").
     */
    private fun startEditAffirmationActivity(affirmationKey: String) {
        val intent = Intent(this, EditAffirmationActivity::class.java).apply {
            // Передаем ключ, чтобы знать, какой текст редактировать и сохранять
            putExtra("AFFIRMATION_KEY", affirmationKey)
        }
        startActivity(intent) // <--- ЭТА СТРОКА РАСКОММЕНТИРОВАНА
    }

    // Хелпер функция для конвертации dp в px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }
}
