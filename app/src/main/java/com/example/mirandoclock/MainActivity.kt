// MainActivity.kt

package com.example.mirandoclock



import android.Manifest

import android.app.Activity

import android.app.WallpaperManager

import android.content.Context

import android.content.Intent

import android.content.pm.PackageManager

import android.graphics.Bitmap

import android.graphics.BitmapFactory

import android.media.AudioManager

import android.media.ToneGenerator

import android.net.Uri

import android.os.Build

import android.os.Bundle

import android.provider.MediaStore

import android.provider.Settings

import android.widget.Button

import android.widget.EditText

import android.widget.ImageView

import android.widget.Switch

import android.widget.TextView

import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts

import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat

import androidx.core.content.ContextCompat

import java.io.IOException



class MainActivity : AppCompatActivity() {



    // Коды запросов для разрешений и выбора изображения

    private val PERMISSION_REQUEST_CODE = 100

    private val PICK_IMAGE_REQUEST_CODE = 101



    // UI элементы

    private lateinit var selectImageButton: Button

    private lateinit var wallpaperPreview: ImageView // Для отображения выбранной картинки в приложении

    private lateinit var affirmationEditText: EditText // Для ввода аффирмации

    private lateinit var setAffirmationButton: Button // Кнопка для сохранения аффирмации

    private lateinit var affirmationTextView: TextView // Для отображения аффирмации поверх картинки

    private lateinit var beeperSwitch: Switch // Переключатель для бипера

    private lateinit var logoImageView: ImageView // Для логотипа



    // Для сохранения данных (аффирмация, состояние бипера, URI картинки)

    private val PREFS_NAME = "MirandoClockPrefs"

    private val KEY_AFFIRMATION = "affirmation_text"

    private val KEY_BEEPER_ENABLED = "beeper_enabled"

    private val KEY_WALLPAPER_URI = "wallpaper_uri"



    // Для генерации звука бипера

    private var toneGenerator: ToneGenerator? = null



    // ActivityResultLauncher для выбора изображения

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {

            val imageUri: Uri? = result.data?.data

            imageUri?.let {

                try {

                    // Сохраняем URI выбранной картинки

                    val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

                    sharedPrefs.edit().putString(KEY_WALLPAPER_URI, it.toString()).apply()



                    // Отображаем выбранную картинку в ImageView

                    wallpaperPreview.setImageURI(it)



                    // Устанавливаем картинку как заставку экрана блокировки

                    setLockScreenWallpaper(it)

                } catch (e: IOException) {

                    e.printStackTrace()

                    Toast.makeText(this, "Не удалось загрузить картинку: ${e.message}", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        // Инициализация UI элементов

        selectImageButton = findViewById(R.id.selectImageButton)

        wallpaperPreview = findViewById(R.id.wallpaperPreview)

        affirmationEditText = findViewById(R.id.affirmationEditText)

        setAffirmationButton = findViewById(R.id.setAffirmationButton)

        affirmationTextView = findViewById(R.id.affirmationTextView)

        beeperSwitch = findViewById(R.id.beeperSwitch)

        logoImageView = findViewById(R.id.logoImageView)



        // Загрузка сохраненных данных

        loadSavedData()



        // Установка слушателя для кнопки выбора изображения

        selectImageButton.setOnClickListener {

            // Проверяем разрешение перед открытием галереи

            if (checkPermission()) {

                openImageChooser()

            } else {

                requestPermission()

            }

        }



        // Установка слушателя для кнопки сохранения аффирмации

        setAffirmationButton.setOnClickListener {

            val affirmationText = affirmationEditText.text.toString()

            if (affirmationText.isNotBlank()) {

                saveAffirmation(affirmationText)

                Toast.makeText(this, "Аффирмация сохранена!", Toast.LENGTH_SHORT).show()

            } else {

                Toast.makeText(this, "Пожалуйста, введите аффирмацию.", Toast.LENGTH_SHORT).show()

            }

        }



        // Установка слушателя для переключателя бипера

        beeperSwitch.setOnCheckedChangeListener { _, isChecked ->

            saveBeeperState(isChecked)

            if (isChecked) {

                Toast.makeText(this, "Бипер включен. Нажмите кнопку 'Тест бипера' для проверки.", Toast.LENGTH_SHORT).show()

                // Добавим кнопку для теста бипера

                val testBeeperButton: Button = findViewById(R.id.testBeeperButton)

                testBeeperButton.visibility = Button.VISIBLE

                testBeeperButton.setOnClickListener {

                    playBeeperSound()

                }

            } else {

                Toast.makeText(this, "Бипер выключен.", Toast.LENGTH_SHORT).show()

                // Скрываем кнопку теста бипера

                findViewById<Button>(R.id.testBeeperButton).visibility = Button.GONE

            }

        }



        // Установка логотипа (простой SVG)

        // В реальном приложении вы бы использовали @drawable/your_logo_svg

        // Для примера, используем простой TextView или ImageView с текстом

        // Мы уже добавили ImageView в XML, но для простоты можем пока просто установить его видимость

        // Если у вас есть SVG-файл, поместите его в res/drawable и используйте logoImageView.setImageResource(R.drawable.your_logo_svg)

        // Для демонстрации, пока просто сделаем его видимым.

        // Если вы хотите буквы "ss", можете использовать TextView поверх ImageView

        // Например:

        // val ssLogoTextView: TextView = findViewById(R.id.ssLogoTextView)

        // ssLogoTextView.text = "ss" // или "©"

        // ssLogoTextView.visibility = View.VISIBLE

        // ssLogoTextView.setTextColor(Color.WHITE) // Или любой другой цвет

        // ssLogoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Размер шрифта

    }



    // Загрузка сохраненных данных при запуске активности

    private fun loadSavedData() {

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)



        // Загрузка и отображение аффирмации

        val savedAffirmation = sharedPrefs.getString(KEY_AFFIRMATION, "")

        affirmationEditText.setText(savedAffirmation)

        affirmationTextView.text = savedAffirmation



        // Загрузка состояния бипера

        val beeperEnabled = sharedPrefs.getBoolean(KEY_BEEPER_ENABLED, false)

        beeperSwitch.isChecked = beeperEnabled

        if (beeperEnabled) {

            findViewById<Button>(R.id.testBeeperButton).visibility = Button.VISIBLE

            findViewById<Button>(R.id.testBeeperButton).setOnClickListener {

                playBeeperSound()

            }

        } else {

            findViewById<Button>(R.id.testBeeperButton).visibility = Button.GONE

        }



        // Загрузка и отображение выбранной картинки

        val savedWallpaperUriString = sharedPrefs.getString(KEY_WALLPAPER_URI, null)

        savedWallpaperUriString?.let {

            try {

                val imageUri = Uri.parse(it)

                wallpaperPreview.setImageURI(imageUri)

            } catch (e: Exception) {

                e.printStackTrace()

                Toast.makeText(this, "Не удалось загрузить сохраненную картинку.", Toast.LENGTH_SHORT).show()

            }

        }

    }



    // Сохранение аффирмации

    private fun saveAffirmation(text: String) {

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPrefs.edit().putString(KEY_AFFIRMATION, text).apply()

        affirmationTextView.text = text // Обновляем отображение аффирмации

    }



    // Сохранение состояния бипера

    private fun saveBeeperState(enabled: Boolean) {

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        sharedPrefs.edit().putBoolean(KEY_BEEPER_ENABLED, enabled).apply()

    }



    // Воспроизведение звука бипера

    private fun playBeeperSound() {

        if (beeperSwitch.isChecked) {

            if (toneGenerator == null) {

                toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100) // 100% громкости

            }

            // Воспроизводим короткий тон DTMF_S_0 (простой короткий гудок)

            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_S_0, 150) // 150 мс

            // Не забываем освободить ресурсы ToneGenerator после использования, чтобы избежать утечек

            // Для простого теста можно не освобождать сразу, но в реальном приложении лучше управлять жизненным циклом

            // toneGenerator?.release()

            // toneGenerator = null

        }

    }



    // Проверка разрешения на чтение внешнего хранилища

    private fun checkPermission(): Boolean {

        return ContextCompat.checkSelfPermission(

            this,

            Manifest.permission.READ_EXTERNAL_STORAGE

        ) == PackageManager.PERMISSION_GRANTED

    }



    // Запрос разрешения на чтение внешнего хранилища

    private fun requestPermission() {

        ActivityCompat.requestPermissions(

            this,

            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),

            PERMISSION_REQUEST_CODE

        )

    }



    // Обработка результата запроса разрешения

    override fun onRequestPermissionsResult(

        requestCode: Int,

        permissions: Array<out String>,

        grantResults: IntArray

    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this, "Разрешение получено!", Toast.LENGTH_SHORT).show()

                openImageChooser()

            } else {

                Toast.makeText(this, "Для выбора картинок нужно разрешение на чтение памяти.", Toast.LENGTH_LONG).show()

                // Предложим пользователю перейти в настройки, если разрешение отклонено навсегда

                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    Toast.makeText(this, "Разрешение отклонено. Пожалуйста, включите его в настройках приложения.", Toast.LENGTH_LONG).show()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)

                    val uri = Uri.fromParts("package", packageName, null)

                    intent.data = uri

                    startActivity(intent)

                }

            }

        }

    }



    // Открытие средства выбора изображений (галереи)

    private fun openImageChooser() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        pickImageLauncher.launch(intent)

    }



    // Установка выбранной картинки как заставки экрана блокировки

    private fun setLockScreenWallpaper(imageUri: Uri) {

        val wallpaperManager = WallpaperManager.getInstance(applicationContext)

        try {

            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

            // Устанавливаем на экран блокировки (LOCK_SCREEN)

            // Для старых API (до N) может потребоваться SET_WALLPAPER_HINTS

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)

                Toast.makeText(this, "Заставка установлена на экран блокировки!", Toast.LENGTH_LONG).show()

            } else {

                // Для старых версий Android, просто устанавливаем как обычные обои

                wallpaperManager.setBitmap(bitmap)

                Toast.makeText(this, "Обои установлены (возможно, на рабочий стол)!", Toast.LENGTH_LONG).show()

            }

        } catch (e: IOException) {

            e.printStackTrace()

            Toast.makeText(this, "Ошибка при установке заставки: ${e.message}", Toast.LENGTH_LONG).show()

            // Дополнительная отладка:

            // Log.e("MirandoClock", "Ошибка при установке обоев: ${e.message}", e)

        }

    }



    override fun onDestroy() {

        super.onDestroy()

        // Освобождаем ресурсы ToneGenerator при уничтожении активности

        toneGenerator?.release()

        toneGenerator = null

    }

}