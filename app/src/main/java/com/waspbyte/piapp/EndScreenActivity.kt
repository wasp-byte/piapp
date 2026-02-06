package com.waspbyte.piapp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Animatable2
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

class EndScreenActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        val accuracyTv = findViewById<TextView>(R.id.accuracy_tv)
        val indexTv = findViewById<TextView>(R.id.index_tv)
        val score = intent.getFloatExtra("SCORE", 1.0f)
        val currentIndex = intent.getIntExtra("CURRENT_INDEX", 0) + 1

        indexTv.text = currentIndex.toString()
        accuracyTv.text = "${"%.2f".format(score * 100f)}%"

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)
        val previousIndex = sharedPref.getInt(getString(R.string.index), 0)

        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val isNewDay = sharedPref.getString(getString(R.string.previous_date), today) != today || sharedPref.getInt(getString(R.string.streak), 0) == 0
        if (score >= 0.9 && previousIndex < currentIndex) {
            sharedPref.edit {
                putInt(getString(R.string.index), currentIndex)
            }
            if (isNewDay) {
                sharedPref.edit {
                    putString(getString(R.string.previous_date), today)
                    val newStreak = sharedPref.getInt(getString(R.string.streak), 0) + 1
                    putInt(
                        getString(R.string.streak),
                        newStreak
                    )
                    if (newStreak > sharedPref.getInt(getString(R.string.best_streak), 0)) {
                        putInt(getString(R.string.best_streak), newStreak)
                    }
                }

                val fireIv = findViewById<ImageView>(R.id.fire_iv)
                fireIv.visibility = View.VISIBLE
                val animatableVector = (fireIv.drawable as Animatable2)
                animatableVector.start()
                animatableVector.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                    override fun onAnimationEnd(drawable: Drawable?) {
                        super.onAnimationEnd(drawable)
                        fireIv.visibility = View.GONE
                    }
                })
            }
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