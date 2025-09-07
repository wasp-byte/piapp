package com.waspbyte.piapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EndScreenActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        val scoreTv = findViewById<TextView>(R.id.score_tv)
        val score = intent.getFloatExtra("SCORE", 1.0f)
        val currentIndex = intent.getIntExtra("CURRENT_INDEX", 0)

        scoreTv.text = "Your score: ${"%.2f".format(score * 100f)}%"
        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)
        val previousIndex = sharedPref.getInt(getString(R.string.index), 0)
        if (score >= 0.9 && previousIndex < currentIndex) {
            with (sharedPref.edit()) {
                putInt(getString(R.string.index), currentIndex)
                apply()
            }
            println(sharedPref.getInt(getString(R.string.index), 0))
        }
    }
}