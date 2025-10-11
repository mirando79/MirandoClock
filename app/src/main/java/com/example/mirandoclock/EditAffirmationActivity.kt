package com.example.mirandoclock

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import com.example.mirandoclock.databinding.ActivityEditAffirmationBinding

/**
 * Activity для редактирования ежедневной или почасовой аффирмации.
 * Загружает и сохраняет текст аффирмации с помощью SharedPreferences,
 * используя ключ, переданный из SettingsActivity.
 */
class EditAffirmationActivity : AppCompatActivity() {

    // Объект View Binding для доступа к элементам макета
    private lateinit var binding: ActivityEditAffirmationBinding

    // Имя файла SharedPreferences, в котором будут храниться все аффирмации
    private val PREFS_NAME = "AffirmationPrefs"

    // Ключ, по которому текст аффирмации будет сохранен/загружен.
    // Его значение будет передано через Intent.
    private var affirmationKey: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализация View Binding и установка макета
        binding = ActivityEditAffirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Получаем данные из Intent, переданные SettingsActivity
        val title = intent.getStringExtra("AFFIRMATION_TITLE") ?: getString(R.string.edit_title_daily)
        affirmationKey = intent.getStringExtra("AFFIRMATION_KEY") ?: "daily_affirmation_text"

        // 2. Устанавливаем заголовок Activity
        binding.editTitleTextView.text = getString(R.string.affirmation_edit_title_format, title)

        // 3. Загружаем текущий текст аффирмации, если он был сохранен ранее
        loadAffirmationText()

        // 4. Настройка слушателей кнопок (Сохранить/Отмена)
        setupEventListeners()
    }

    /**
     * Загружает сохраненный текст аффирмации из SharedPreferences в EditText.
     */
    private fun loadAffirmationText() {
        if (affirmationKey.isNotEmpty()) {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedText = prefs.getString(affirmationKey, "")
            binding.affirmationEditText.setText(savedText)
            // Перемещаем курсор в конец текста для удобства редактирования
            binding.affirmationEditText.setSelection(savedText?.length ?: 0)
        }
    }

    /**
     * Сохраняет текст аффирмации из EditText в SharedPreferences.
     */
    private fun saveAffirmationText() {
        if (affirmationKey.isNotEmpty()) {
            // Получаем текст, удаляя лишние пробелы в начале/конце
            val newAffirmation = binding.affirmationEditText.text.toString().trim()
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            // Сохраняем новое значение по ключу
            with(prefs.edit()) {
                putString(affirmationKey, newAffirmation)
                apply() // Применяем изменения асинхронно
            }
        }
    }

    /**
     * Настраивает обработчики нажатий для кнопок "Сохранить" и "Отмена".
     */
    private fun setupEventListeners() {
        // Кнопка "Отмена" - просто закрывает Activity
        binding.cancelButton.setOnClickListener {
            finish()
        }

        // Кнопка "Сохранить" - сохраняет данные и закрывает Activity
        binding.saveButton.setOnClickListener {
            saveAffirmationText()
            // Важно: finish() закрывает текущее Activity и возвращает на предыдущий экран (SettingsActivity)
            finish()
        }
    }
}
