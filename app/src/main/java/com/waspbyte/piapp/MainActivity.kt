package com.waspbyte.piapp

import PiManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
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
        val index = sharedPref.getInt(getString(R.string.index), 0)

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurfaceVariant, typedValue, true
        )
        val colorLearned = typedValue.data
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValue, true
        )
        val colorNew = typedValue.data
        val span = SpannableString(piManager.PI.slice(0..index))
        span.setSpan(ForegroundColorSpan(colorLearned), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        span.setSpan(ForegroundColorSpan(colorNew), index, index+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val piTv = findViewById<TextView>(R.id.pi_tv)
        piTv.text = span
        // TODO
        piTv.append(piManager.PI.slice(index+1..10000))
        val todayTv = findViewById<TextView>(R.id.today_tv)
        todayTv.text = (index + 1).toString()

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    public override fun onRestart() {
        super.onRestart()
        recreate()
    }
}