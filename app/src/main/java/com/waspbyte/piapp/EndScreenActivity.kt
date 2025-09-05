package com.waspbyte.piapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EndScreenActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        val scoreTv = findViewById<TextView>(R.id.score_tv)
        val score = intent.getFloatExtra("SCORE", 1.0f)

        scoreTv.text = "Your score: ${"%.2f".format(score * 100f)}%"
    }
}