package com.waspbyte.piapp

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.LineChartView

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
        var ratio = 0.0f
        if (bestStreak != 0)
            ratio = currentStreak.toFloat() / bestStreak.toFloat() * 100f
        bestStreakDcv.animate(listOf(ratio))
        bestStreakDcv.animation.duration = 5000L

        val highScoreChart = findViewById<LineChartView>(R.id.highscore_lcv)
        highScoreChart.gradientFillColors =
            intArrayOf(
                colorTertiary,
                Color.TRANSPARENT
            )
        highScoreChart.animation.duration = 5000L
        highScoreChart.animate(scoreRepository.getHighScores())
    }
}