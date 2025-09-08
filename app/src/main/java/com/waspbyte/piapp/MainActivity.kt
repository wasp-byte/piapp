package com.waspbyte.piapp

import PiManager
import android.content.Intent
import android.os.Bundle
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

        val piManager = PiManager(this)

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)

        val piTv = findViewById<TextView>(R.id.pi_tv)
        // TODO
        piTv.text = piManager.PI.slice(0..10000)
        val todayTv = findViewById<TextView>(R.id.today_tv)
        todayTv.text = (sharedPref.getInt(getString(R.string.index), 0) + 1).toString()

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    public override fun onRestart() {
        super.onRestart()
        recreate()
    }
}