package com.example.mirandoclock

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat

class RuneSelectorActivity : AppCompatActivity() {

    private val runes = arrayOf(
        "ᚠ","ᚢ","ᚦ","ᚨ","ᚱ","ᚲ",
        "ᚷ","ᚹ","ᚺ","ᚾ","ᛁ","ᛃ",
        "ᛇ","ᛈ","ᛉ","ᛋ","ᛏ","ᛒ",
        "ᛖ","ᛗ","ᛚ","ᛜ","ᛞ","ᛟ"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val grid = GridLayout(this).apply {
            rowCount = 6
            columnCount = 4
            setPadding(16, 16, 16, 16)
        }

        val runeFont = ResourcesCompat.getFont(this, R.font.runic)

        runes.forEach { rune ->
            val btn = Button(this).apply {
                text = rune
                textSize = 24f
                typeface = runeFont
                setPadding(8, 8, 8, 8)
                setOnClickListener {
                    saveRune(rune)
                    finish()
                }
            }
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(8, 8, 8, 8)
            }
            grid.addView(btn, params)
        }

        setContentView(grid)
    }

    private fun saveRune(rune: String) {
        getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit()
            .putString("selected_rune", rune)
            .apply()
    }
}
