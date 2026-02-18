/**
 * piapp application for learning numbers of pi
 * Copyright (C) 2026 WaspByte
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 **/
package com.waspbyte.piapp

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var piManager: PiManager
    private lateinit var piAdapter: PiAdapter
    private lateinit var streakBtn: MaterialButton
    private lateinit var scoreRepository: ScoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        piManager = PiManager(this)
        scoreRepository = ScoreRepository(this)

        val index = scoreRepository.getCurrentIndex()
        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorOnSurface, typedValue, true
        )
        val colorLearned = typedValue.data
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorTertiary, typedValue, true
        )
        val colorNew = typedValue.data
        val piItem = layoutInflater.inflate(R.layout.pi_item, null)
        val indexWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 48f, resources.displayMetrics)
        val paint = piItem.findViewById<TextView>(R.id.pi_tv).paint
        val piCv = findViewById<CardView>(R.id.pi_cv)
        val piLl = findViewById<LinearLayout>(R.id.pi_ll)
        val charWidth = paint.measureText("0")
        val width =
            Resources.getSystem().displayMetrics.widthPixels - (piCv.marginStart * 2 + piLl.marginEnd) - indexWidth
        piAdapter = PiAdapter(
            piManager.piWithDot(),
            index + if (index >= piManager.DOT) 1 else 0,
            colorLearned,
            colorNew,
            (width / charWidth).toInt()
        )
        val recyclerView: RecyclerView = findViewById(R.id.pi_rv)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = piAdapter
        val highscoreTv = findViewById<TextView>(R.id.highscore_tv)
        highscoreTv.text = index.toString()

        findViewById<MaterialButton>(R.id.to_game_btn).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        streakBtn = findViewById<MaterialButton>(R.id.streak_btn)
        val streak = scoreRepository.getCurrentStreak()
        if (streak > 0) {
            streakBtn.setIconTintResource(R.color.theme_secondary)
        }
        streakBtn.text = streak.toString()
        streakBtn.setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }

    public override fun onRestart() {
        super.onRestart()
        val index = scoreRepository.getCurrentIndex()
        val highscoreTv = findViewById<TextView>(R.id.highscore_tv)
        highscoreTv.text = index.toString()
        piAdapter.updateIndex(index + if (index >= piManager.DOT) 1 else 0)
        val streak = scoreRepository.getCurrentStreak()
        if (streak > 0) {
            streakBtn.setIconTintResource(R.color.theme_secondary)
        }
        streakBtn.text = streak.toString()
    }

    class PiAdapter(
        private val text: String,
        private var index: Int,
        private val colorLearned: Int,
        private val colorNew: Int,
        private val charsPerLine: Int
    ) : RecyclerView.Adapter<PiAdapter.ViewHolder>() {
        private var dataSet = Array(text.length / charsPerLine) { i ->
            val span = SpannableString(text.slice(i * charsPerLine..<(i + 1) * charsPerLine))
            if (i * charsPerLine <= index) {
                span.setSpan(
                    ForegroundColorSpan(colorLearned),
                    0,
                    min(index - i * charsPerLine, charsPerLine),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (index + 1 <= (i + 1) * charsPerLine) {
                    span.setSpan(
                        ForegroundColorSpan(colorNew),
                        index - i * charsPerLine,
                        index - i * charsPerLine + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            span
        }

        fun updateIndex(indexNew: Int) {
            if (indexNew == index) return
            index = indexNew
            val dataSetIndex = index / charsPerLine
            for (i in 0..(indexNew / charsPerLine) - dataSetIndex) {
                val currentIndex = dataSetIndex + i
                val span = dataSet[currentIndex]
                // TODO boilerplate
                span.setSpan(
                    ForegroundColorSpan(colorLearned), 0, min(
                        indexNew - currentIndex * charsPerLine,
                        charsPerLine
                    ), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (indexNew + 1 <= (currentIndex + 1) * charsPerLine) {
                    span.setSpan(
                        ForegroundColorSpan(colorNew),
                        indexNew - currentIndex * charsPerLine,
                        indexNew - currentIndex * charsPerLine + 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                dataSet[currentIndex] = span
            }
            notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val piTv: TextView = view.findViewById(R.id.pi_tv)
            val indexTv: TextView = view.findViewById(R.id.index_tv)
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(viewGroup.context).inflate(R.layout.pi_item, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.piTv.text = dataSet[position]

            val index = position * charsPerLine + (if (position > 0) -1 else 0) + (charsPerLine - 1)
            var s = ""
            if (index < 1000 && index % 100 < charsPerLine) {
                if (index >= 100)
                    s = (index / 100 * 100).toString()
            } else if (index % 1000 < charsPerLine) {
                s = "${index / 1000}k"
            }
            viewHolder.indexTv.text = s
        }

        override fun getItemCount() = dataSet.size

    }
}