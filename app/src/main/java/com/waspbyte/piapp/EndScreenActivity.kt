package com.waspbyte.piapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

        val accuracyTv = findViewById<TextView>(R.id.accuracy_tv)
        val indexTv = findViewById<TextView>(R.id.index_tv)
        val streakTv = findViewById<TextView>(R.id.streak_tv)
        val score = intent.getFloatExtra("SCORE", 1.0f)
        val currentIndex = intent.getIntExtra("CURRENT_INDEX", 0)

        indexTv.text = "Reached: $currentIndex"
        accuracyTv.text = "Accuracy: ${"%.2f".format(score * 100f)}%"

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)
        val previousIndex = sharedPref.getInt(getString(R.string.index), 0)

        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val isNewDay = sharedPref.getString(getString(R.string.previous_date), today) != today || sharedPref.getInt(getString(R.string.streak), 0) == 0
        if (score >= 0.9 && previousIndex < currentIndex) {
            with (sharedPref.edit()) {
                putInt(getString(R.string.index), currentIndex)
                apply()
            }
            if (isNewDay) {
                with(sharedPref.edit()) {
                    putString(getString(R.string.previous_date), today)
                    putInt(getString(R.string.streak), sharedPref.getInt(getString(R.string.streak), 0) + 1)
                    apply()
                }

                streakTv.text = "Reached daily goal!"
            }
        } else if (isNewDay) {
            streakTv.text = "Failed to reach daily goal."
        }

        findViewById<Button>(R.id.back_btn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.again_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
    }
}