package com.waspbyte.piapp

import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.db.williamchart.view.DonutChartView

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValue, true
        )
        val colorPrimary = typedValue.data
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValue, true
        )
        val colorTertiary = typedValue.data

        val scoreRepository = ScoreRepository(this)
        val currentStreak = scoreRepository.getCurrentStreak()
        val bestStreak = scoreRepository.getBestStreak()

        val bestStreakDcv = findViewById<DonutChartView>(R.id.best_streak_dcv)
        bestStreakDcv.donutColors = intArrayOf(
            colorPrimary
        )
        val ratio = currentStreak.toFloat() / bestStreak.toFloat() * 100f
        bestStreakDcv.animate(listOf(ratio))
        bestStreakDcv.animation.duration = 5000L
    }
}