package com.waspbyte.piapp

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.db.williamchart.data.Scale
import com.db.williamchart.view.DonutChartView
import com.db.williamchart.view.LineChartView
import kotlinx.datetime.DayOfWeek

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

        findViewById<Button>(R.id.back_btn).setOnClickListener {
            finish()
        }

        val bestStreakDcv = findViewById<DonutChartView>(R.id.best_streak_dcv)
        bestStreakDcv.donutColors = intArrayOf(
            colorPrimary
        )
        var ratio = 0.0f
        if (bestStreak != 0)
            ratio = currentStreak.toFloat() / bestStreak.toFloat() * 100f
        bestStreakDcv.animate(listOf(ratio))
        bestStreakDcv.animation.duration = 1000L

        val currentStreakTv = findViewById<TextView>(R.id.streak_tv)
        currentStreakTv.text = currentStreak.toString()

        val highScoreChart = findViewById<LineChartView>(R.id.highscore_lcv)
        highScoreChart.gradientFillColors =
            intArrayOf(
                colorTertiary,
                Color.TRANSPARENT
            )
        highScoreChart.animation.duration = 1000L

        val highScoreRangesS = findViewById<Spinner>(R.id.highscore_ranges_s)
        val highScoreRangesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.highscore_ranges,
            android.R.layout.simple_spinner_item
        )
        highScoreRangesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        highScoreRangesS.adapter = highScoreRangesAdapter
        highScoreRangesS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,
                position: Int,
                id: Long
            ) {
                val fromDate = when (highScoreRangesAdapter.getItem(position)) {
                    getString(R.string.all) -> 0
                    getString(R.string.year) -> System.currentTimeMillis() / 1000L - 60 * 60 * 24 * 365
                    getString(R.string.month) -> System.currentTimeMillis() / 1000L - 60 * 60 * 24 * 30
                    getString(R.string.week) -> System.currentTimeMillis() / 1000L - 60 * 60 * 24 * 7
                    else -> throw NoWhenBranchMatchedException()
                }
                val highScores = scoreRepository.getHighScores(fromDate)
                highScoreChart.scale = Scale(0f, highScores.last().second * 1.2f)
                highScoreChart.animate(highScores)
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {
            }
        }

        val heatMapView = findViewById<ComposeView>(R.id.heatmap_cv)
        heatMapView.setContent {
            HeatmapSection(DayOfWeek.MONDAY, scoreRepository.getHeatmap())
        }
    }
}