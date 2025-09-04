package com.waspbyte.piapp

import PiManager
import android.content.res.Resources
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginStart

class GameActivity : AppCompatActivity() {
    private lateinit var piManager: PiManager
    private lateinit var digitsTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        digitsTv = findViewById<TextView>(R.id.digits_tv)
        val width = Resources.getSystem().displayMetrics.widthPixels
        val availableWidth = width - digitsTv.marginStart - digitsTv.marginEnd
        piManager = PiManager(digitsTv, availableWidth)
    }

    fun addText(view: View) {
        piManager.next((view as Button).text[0])
        piManager.check(view.text[0])
        formatText(piManager.getColors())
    }

    fun delete(view: View) {
        piManager.back()
        formatText(piManager.getColors())
    }

    private fun formatText(colors: List<Int>) {
        val text = piManager.getText()
        if (text.isEmpty()) {
            digitsTv.text = ""
            return
        }

        val spannable = SpannableStringBuilder()
        text.forEachIndexed { i, char ->
            val color = colors[i]
            val span = SpannableString(char.toString()).apply {
                setSpan(
                    ForegroundColorSpan(color), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            spannable.append(span)
        }

        digitsTv.text = spannable
    }
}
