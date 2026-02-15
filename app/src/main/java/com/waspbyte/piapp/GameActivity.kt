package com.waspbyte.piapp

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginEnd
import androidx.core.view.marginStart

class GameActivity : AppCompatActivity() {
    private lateinit var piManager: PiManager
    private lateinit var digitsTv: TextView

    private var maxTextWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        digitsTv = findViewById(R.id.digits_tv)

        findViewById<Button>(R.id.back_btn).setOnClickListener {
            finish()
        }

        val width = Resources.getSystem().displayMetrics.widthPixels
        maxTextWidth = width - digitsTv.marginStart - digitsTv.marginEnd
        piManager = PiManager(this)
    }

    fun addText(view: View) {
        piManager.next((view as Button).text[0], digitsTv, maxTextWidth)
        piManager.check(view.text[0])
        formatText()
    }

    fun delete(view: View) {
        piManager.back()
        formatText()
    }

    fun done(view: View) {
        val intent = Intent(this, EndScreenActivity::class.java)
        intent.putExtra(getString(R.string.accuracy), piManager.getScore())
        intent.putExtra(getString(R.string.current_index), piManager.getCurrentIndex() + 1)
        startActivity(intent)
        finish()
    }


    private fun formatText() {
        val typedValue = TypedValue()
        theme.resolveAttribute(
            com.google.android.material.R.attr.colorError, typedValue, true
        )
        val text = piManager.getText()
        if (text.isEmpty()) {
            digitsTv.text = ""
            return
        }
        val colors = piManager.getColors()

        digitsTv.text = ""

        text.forEachIndexed { i, char ->
            val color = colors[i]
            val span = SpannableString(char.toString())
            if (color != Color.BLACK) {
                span.setSpan(ForegroundColorSpan(typedValue.data), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            digitsTv.append(span)
        }
    }

    override fun onPause() {
        super.onPause()
        piManager = PiManager(this)
        digitsTv.text = ""
    }
}
