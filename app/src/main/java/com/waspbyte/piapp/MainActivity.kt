package com.waspbyte.piapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val piTv = findViewById<TextView>(R.id.pi_tv)

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            Log.d("BUTTONS", "BUTTON!")
            startActivity(Intent(this, GameActivity::class.java))
        }

        findViewById<Button>(R.id.show_more_btn).setOnClickListener {
            startActivity(Intent(this, PiActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this, piTv, "pi").toBundle())
        }
    }
}