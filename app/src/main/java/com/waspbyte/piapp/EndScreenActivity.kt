package com.waspbyte.piapp

import android.annotation.SuppressLint
import android.os.Bundle
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
            val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            if (sharedPref.getString(getString(R.string.previous_date), today) != today) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.previous_date), today)
                    putInt(getString(R.string.streak), sharedPref.getInt(getString(R.string.streak), 0) + 1)
                    apply()
                }
            } else if (sharedPref.getInt(getString(R.string.streak), 0) == 0) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.previous_date), today)
                    putInt(getString(R.string.streak), 1)
                    apply()
                }
            }
        }
    }
}