package com.waspbyte.piapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
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

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)

        val piTv = findViewById<TextView>(R.id.pi_tv)
        val todayTv = findViewById<TextView>(R.id.today_tv)
        todayTv.text = (sharedPref.getInt(getString(R.string.index), 0) + 1).toString()

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        findViewById<Button>(R.id.show_more_btn).setOnClickListener {
            startActivity(Intent(this, PiActivity::class.java), ActivityOptions.makeSceneTransitionAnimation(this, piTv, "pi").toBundle())
        }
    }

    public override fun onRestart() {
        super.onRestart()
        recreate()
    }
}