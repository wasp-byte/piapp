package com.waspbyte.piapp

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import com.db.williamchart.view.DonutChartView

class StatsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        val sharedPref = getSharedPreferences(getString(R.string.prefs), MODE_PRIVATE)

        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary, typedValue, true
        )
        val colorPrimary = typedValue.data
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValue, true
        )
        val colorTertiary = typedValue.data

        val streak = sharedPref.getInt(getString(R.string.streak), 0)
        val bestStreak = sharedPref.getInt(getString(R.string.best_streak), 1)

        val bestStreakDcv = findViewById<DonutChartView>(R.id.best_streak_dcv)
        bestStreakDcv.donutColors = intArrayOf(
            colorPrimary
        )
        val ratio = streak.toFloat() / bestStreak.toFloat() * 100f
        bestStreakDcv.animate(listOf(ratio))
        bestStreakDcv.animation.duration = 5000L
    }
}