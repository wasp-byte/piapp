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

class EndScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_screen)

        val accuracyTv = findViewById<TextView>(R.id.accuracy_tv)
        val indexTv = findViewById<TextView>(R.id.index_tv)
        val accuracy = intent.getFloatExtra(getString(R.string.accuracy), 1.0f)
        val currentIndex = intent.getIntExtra(getString(R.string.current_index), 1)

        val scoreRepository = ScoreRepository(this)
        val previousStreak = scoreRepository.getCurrentStreak()
        scoreRepository.saveAttempt(currentIndex, accuracy)
        val currentStreak = scoreRepository.getCurrentStreak()

        indexTv.text = currentIndex.toString()
        accuracyTv.text = getString(R.string.format_accuracy).format(accuracy * 100f)

        if (currentStreak > previousStreak) {
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

        findViewById<Button>(R.id.back_btn).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.again_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
            finish()
        }
    }
}