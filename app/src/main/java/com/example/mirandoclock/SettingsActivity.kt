package com.example.mirandoclock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val PREFS_NAME = "AffirmationPrefs"

    // Список названий знаков Зодиака для динамического создания элементов
    private val zodiacSigns = arrayOf(
        "Овен", "Телец", "Близнецы", "Рак", "Лев", "Дева",
        "Весы", "Скорпион", "Стрелец", "Козерог", "Водолей", "Рыбы"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Инициализация главной аффирмации дня
        initDailyAffirmation()

        // 2. Динамическое создание 12 ежечасных аффирмаций
        initHourlyAffirmations()
    }

    /**
     * Инициализирует логику для главной аффирмации дня.
     * Устанавливает слушатель для кнопки "📝".
     */
    private fun initDailyAffirmation() {
        val key = "AFFIRMATION_DAILY"
        val title = getString(R.string.edit_title_daily)

        // Отображение текущего текста аффирмации в TextView
        binding.dailyAffirmationTextView.text = getCurrentAffirmationText(key)

        // Обработчик кнопки редактирования главной аффирмации
        binding.dailyAffirmationEditButton.setOnClickListener {
            launchEditActivity(key, title)
        }
    }

    /**
     * Динамически создает 12 ячеек для ежечасных аффирмаций (знаков Зодиака).
     */
    private fun initHourlyAffirmations() {
        val container = binding.hourlySettingsContainer
        val inflater = LayoutInflater.from(this)

        // Удаляем макет-заготовку (первую тестовую ячейку)
        container.removeAllViews()

        for (i in 1..12) {
            val hour = i
            val sign = zodiacSigns[i - 1]
            val key = "AFFIRMATION_HOUR_$hour"
            val title = getString(R.string.hourly_affirmation_title_format, hour, sign)

            // Используем LayoutInflater для создания нового элемента из XML-ресурса
            // Так как у нас нет отдельного ресурса для одной строки, я сымитирую это
            // созданием простого контейнера. В реальном приложении тут использовался бы отдельный layout.
            // Вместо этого, я создаю простой TextView и кнопку для примера.

            val itemLayout = inflater.inflate(R.layout.settings_hourly_item_layout, container, false)

            // Получаем ссылки на элементы внутри динамически созданного макета
            val labelTextView = itemLayout.findViewById<TextView>(R.id.hourlyLabel)
            val editButton = itemLayout.findViewById<Button>(R.id.hourlyAffirmationEditButton)

            // Установка текста: "1: Овен (Текущая аффирмация)"
            val currentText = getCurrentAffirmationText(key).take(30) + if (getCurrentAffirmationText(key).length > 30) "..." else ""
            labelTextView.text = "$hour: $sign\n($currentText)"

            // Настройка слушателя для кнопки редактирования
            editButton.setOnClickListener {
                launchEditActivity(key, title)
            }

            // Добавляем созданный элемент в контейнер
            container.addView(itemLayout)
        }
    }

    /**
     * Запускает EditAffirmationActivity с заданным ключом и заголовком.
     */
    private fun launchEditActivity(key: String, title: String) {
        val intent = Intent(this, EditAffirmationActivity::class.java).apply {
            putExtra("AFFIRMATION_KEY", key)
            putExtra("AFFIRMATION_TITLE", title)
        }
        startActivity(intent)
    }

    /**
     * Получает текущий сохраненный текст аффирмации.
     */
    private fun getCurrentAffirmationText(key: String): String {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Возвращаем пустую строку, если текст не найден
        return prefs.getString(key, "Нажмите, чтобы изменить") ?: "Нажмите, чтобы изменить"
    }

    // Для запуска hourlyAffirmations нам нужен XML-макет для одной строки
    // Создаем минимальный макет для элемента списка
    // (Иначе itemLayout не будет иметь нужных ID)
}
