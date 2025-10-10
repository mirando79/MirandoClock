package com.example.mirandoclock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivitySettingsBinding
import kotlin.math.minOf // ИСПРАВЛЕНИЕ: Добавлен необходимый импорт для minOf

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    // Список названий знаков зодиака в порядке часов (1-12)
    private val ZODIAC_SIGNS = listOf(
        "Водолей", "Рыбы", "Овен", "Телец", "Близнецы", "Рак",
        "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог"
    )

    // Ключи для SharedPreferences
    private val PREFS_NAME = "AffirmationPrefs"
    private val KEY_DAILY_AFFIRMATION = "daily_affirmation_text"
    private val KEY_DAILY_CHECKED = "daily_affirmation_checked"
    private val KEY_HOURLY_TEXT_PREFIX = "hourly_affirmation_text_"
    private val KEY_HOURLY_CHECKED_PREFIX = "hourly_affirmation_checked_"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Инициализация и отображение списка аффирмаций по часам
        setupHourlyAffirmations()

        // 2. Инициализация аффирмации дня
        setupDailyAffirmation()

        // 3. Настройка обработчиков нажатий для дополнительных кнопок
        setupExtraButtons()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем текст аффирмации дня при возвращении с экрана редактирования
        loadAffirmationText(KEY_DAILY_AFFIRMATION, binding.dailyAffirmationTextView, getString(R.string.daily_affirmation_placeholder))

        // Обновляем тексты почасовых аффирмаций
        updateHourlyAffirmationTexts()
    }

    /**
     * Загружает сохраненный текст аффирмации и устанавливает его в TextView.
     */
    private fun loadAffirmationText(key: String, textView: TextView, defaultText: String) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedText = prefs.getString(key, null)

        textView.text = if (!savedText.isNullOrEmpty()) {
            savedText
        } else {
            defaultText
        }
    }

    /**
     * Настраивает элемент "Аффирмация дня".
     */
    private fun setupDailyAffirmation() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Загрузка и установка текста
        loadAffirmationText(KEY_DAILY_AFFIRMATION, binding.dailyAffirmationTextView, getString(R.string.daily_affirmation_placeholder))

        // Загрузка состояния чекбокса
        val isChecked = prefs.getBoolean(KEY_DAILY_CHECKED, true) // По умолчанию включено
        binding.dailyAffirmationCheckbox.isChecked = isChecked

        // Обработчик чекбокса
        binding.dailyAffirmationCheckbox.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(KEY_DAILY_CHECKED, checked).apply()
        }

        // Обработчик кнопки "Записи" (редактирование)
        binding.dailyAffirmationEditButton.setOnClickListener {
            launchEditActivity(KEY_DAILY_AFFIRMATION, getString(R.string.edit_title_daily))
        }

        // TODO: Обработчик кнопки "Обзор"
        binding.dailyImageButton.setOnClickListener {
            // Реализация обзора аффирмации дня
        }
    }

    /**
     * Создает и добавляет 12 элементов для почасовых аффирмаций.
     */
    private fun setupHourlyAffirmations() {
        // Устраняем баг с дублированием: удаляем все ранее добавленные View
        binding.hourlySettingsContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        for (i in 0 until 12) {
            val hour = i + 1
            val sign = ZODIAC_SIGNS[i]
            // Ключи для SharedPreferences для текущего часа
            val textKey = KEY_HOURLY_TEXT_PREFIX + hour
            val checkedKey = KEY_HOURLY_CHECKED_PREFIX + hour

            // Создаем новый View из макета settings_hourly_item.xml
            val hourlyItemView = inflater.inflate(R.layout.settings_hourly_item, binding.hourlySettingsContainer, false)

            // Находим элементы внутри созданного View
            val checkbox = hourlyItemView.findViewById<CheckBox>(R.id.hourlyCheckbox)
            val label = hourlyItemView.findViewById<TextView>(R.id.hourlyLabel)
            val editButton = hourlyItemView.findViewById<Button>(R.id.hourlyAffirmationEditButton)
            val reviewButton = hourlyItemView.findViewById<Button>(R.id.hourlyImageButton) // Кнопка "Обзор"

            // 1. Устанавливаем метку (час и знак)
            label.text = getString(R.string.hourly_label_format, hour, sign)

            // 2. Загрузка и установка состояния чекбокса
            val isChecked = prefs.getBoolean(checkedKey, false) // По умолчанию выключено
            checkbox.isChecked = isChecked
            checkbox.tag = checkedKey // Используем tag для хранения ключа

            // 3. Обработчик чекбокса: сохранение состояния
            checkbox.setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean(checkedKey, checked).apply()
            }

            // 4. Обработчик кнопки "Записи" (редактирование)
            editButton.setOnClickListener {
                // ИСПРАВЛЕНИЕ: Передаем только 'sign', так как строковый ресурс ожидает один %s
                val title = getString(R.string.edit_title_hourly_format, sign)
                launchEditActivity(textKey, title)
            }

            // 5. TODO: Обработчик кнопки "Обзор"
            reviewButton.setOnClickListener {
                // Реализация обзора почасовой аффирмации
            }

            // Добавляем созданный элемент в контейнер
            binding.hourlySettingsContainer.addView(hourlyItemView)
        }
    }

    /**
     * Обновляет текст почасовых аффирмаций, вызывается в onResume.
     */
    private fun updateHourlyAffirmationTexts() {
        for (i in 0 until 12) {
            val hour = i + 1
            val textKey = KEY_HOURLY_TEXT_PREFIX + hour

            // Получаем элемент View для текущего часа
            val hourlyItemView = binding.hourlySettingsContainer.getChildAt(i)
            if (hourlyItemView != null) {
                val label = hourlyItemView.findViewById<TextView>(R.id.hourlyLabel)
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val savedText = prefs.getString(textKey, "")

                // Обновляем метку: добавляем превью текста к часу/знаку
                val sign = ZODIAC_SIGNS[i]
                val baseLabel = getString(R.string.hourly_label_format, hour, sign)

                // Показываем первые 20 символов аффирмации, если она сохранена
                label.text = if (!savedText.isNullOrEmpty()) {
                    // ИСПРАВЛЕНИЕ: minOf теперь доступен благодаря импорту
                    val preview = savedText.substring(0, minOf(savedText.length, 20))
                    "$baseLabel: $preview..."
                } else {
                    baseLabel
                }
            }
        }
    }

    /**
     * Запускает EditAffirmationActivity.
     */
    private fun launchEditActivity(key: String, title: String) {
        val intent = Intent(this, EditAffirmationActivity::class.java).apply {
            putExtra("AFFIRMATION_KEY", key)
            putExtra("AFFIRMATION_TITLE", title)
        }
        startActivity(intent)
    }

    /**
     * Настраивает обработчики для кнопок из дополнительной секции.
     */
    private fun setupExtraButtons() {
        // TODO: Обработчик для Ночного режима
        binding.nightModeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            // Сохранение состояния
        }

        // TODO: Обработчик для Длительности подсветки
        binding.screenTimeButton.setOnClickListener {
            // Открытие диалога для выбора длительности
        }

        // TODO: Обработчик для Настройки цвета, шрифта и звука
        binding.colorFontSoundButton.setOnClickListener {
            // Открытие нового Activity для этих настроек
        }
    }
}
