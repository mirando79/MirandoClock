package com.example.mirandoclock

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mirandoclock.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val PREFS_NAME = "AffirmationPrefs"

    // Порядок знаков — при желании можно поменять на любой
    private val zodiacSigns = arrayOf(
        "Водолей", "Рыбы", "Овен", "Телец", "Близнецы", "Рак",
        "Лев", "Дева", "Весы", "Скорпион", "Стрелец", "Козерог"
    )


    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDailyAffirmation()
        initHourlyAffirmations()
        initBeepAndSleepSwitches()

    }

    private fun initDailyAffirmation() {
        val key = "AFFIRMATION_DAILY"
        val title = getString(R.string.edit_title_daily)

        binding.dailyAffirmationTextView.text = getCurrentAffirmationText(key)

        binding.dailyAffirmationEditButton.setOnClickListener {
            launchEditActivity(key, title)
        }

        // пример: добавить кнопку play рядом с dailyAffirmation (если она есть в layout)
        binding.dailyAffirmationTextView.setOnClickListener {
            // воспроизведение файла из res/raw: имя hour_daily.mp3 или другой
            playAudioResourceByName("daily_affirmation")
        }
    }

    private fun initHourlyAffirmations() {
        val container = binding.hourlySettingsContainer
        val inflater = LayoutInflater.from(this)

        container.removeAllViews()

        for (i in 1..12) {
            val hour = i
            val sign = zodiacSigns[i - 1]
            val key = "AFFIRMATION_HOUR_$hour"
            val title = getString(R.string.hourly_affirmation_title_format, hour, sign)

            // inflate по resource id — в Kotlin Ambiguity пропадает, если передаём int
            val itemLayout = inflater.inflate(R.layout.settings_hourly_item, container, false)

            val labelTextView = itemLayout.findViewById<TextView>(R.id.hourlyLabel)
            val editButton = itemLayout.findViewById<Button>(R.id.hourlyAffirmationEditButton)
            val playButton = itemLayout.findViewById<Button?>(R.id.hourlyPlayButton) // может быть null

            val currentTextFull = getCurrentAffirmationText(key)
            val currentText = if (currentTextFull.length > 30) currentTextFull.take(30) + "..." else currentTextFull
            labelTextView.text = "$hour: $sign\n($currentText)"

            editButton.setOnClickListener {
                launchEditActivity(key, title)
            }

            // play button: ищем ресурс в res/raw с именем hour_<hour> либо hourly_<hour>
            playButton?.setOnClickListener {
                val candidateName = "hour_$hour" // например res/raw/hour_1.mp3
                if (!playAudioResourceByName(candidateName)) {
                    // fallback — искать по другому шаблону
                    playAudioResourceByName("hourly_$hour")
                }
            }

            container.addView(itemLayout)
        }
    }

    private fun initBeepAndSleepSwitches() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Настройка бипера
        binding.beepSwitch.isChecked = prefs.getBoolean("BEEP_ENABLED", true)
        binding.beepSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("BEEP_ENABLED", isChecked).apply()
        }

        // Настройка ночного режима
        binding.sleepSwitch.isChecked = prefs.getBoolean("SLEEP_MODE", true)
        binding.sleepSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("SLEEP_MODE", isChecked).apply()
        }
    }

    private fun launchEditActivity(key: String, title: String) {
        val intent = Intent(this, EditAffirmationActivity::class.java).apply {
            putExtra("AFFIRMATION_KEY", key)
            putExtra("AFFIRMATION_TITLE", title)
        }
        startActivity(intent)
    }

    private fun getCurrentAffirmationText(key: String): String {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(key, "Нажмите, чтобы изменить") ?: "Нажмите, чтобы изменить"
    }

    /**
     * Попробовать воспроизвести аудиофайл из res/raw по имени (без расширения).
     * Возвращает true, если найден и запущен.
     */
    private fun playAudioResourceByName(name: String): Boolean {
        // освобождаем предыдущий плеер
        mediaPlayer?.let {
            it.stop()
            it.release()
            mediaPlayer = null
        }

        val resId = resources.getIdentifier(name, "raw", packageName)
        if (resId == 0) {
            Toast.makeText(this, getString(R.string.audio_not_found), Toast.LENGTH_SHORT).show()
            return false
        }

        mediaPlayer = MediaPlayer.create(this, resId)
        mediaPlayer?.setOnCompletionListener {
            it.release()
            mediaPlayer = null
        }
        mediaPlayer?.start()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
