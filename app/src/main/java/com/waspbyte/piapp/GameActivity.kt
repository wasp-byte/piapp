package com.waspbyte.piapp

import PiManager
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.KeyEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly

class GameActivity : AppCompatActivity() {
    private lateinit var piManager: PiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        piManager = PiManager()

        val digitsEt = findViewById<EditText>(R.id.digits_et)
        val pastDigitsTv = findViewById<TextView>(R.id.past_digits_tv)

        digitsEt.requestFocus()

        digitsEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                piManager.back()
                formatText(piManager.getColors(), pastDigitsTv)
            }
            false
        }

        digitsEt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) return
                if (!s.isDigitsOnly()) {
                    digitsEt.text.clear()
                    return
                }

                piManager.next(s[0], pastDigitsTv)
                piManager.check(s[0])

                digitsEt.text.clear()

                formatText(piManager.getColors(), pastDigitsTv)
                println(piManager.getPoints())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun formatText(colors: List<Int>, textView: TextView) {
        val text = piManager.getText()
        if (text.isEmpty()) {
            textView.text = ""
            return
        }

        val spannable = SpannableStringBuilder()
        text.forEachIndexed { i, char ->
            val color = colors[i]
            val span = SpannableString(char.toString()).apply {
                setSpan(
                    ForegroundColorSpan(color),
                    0, length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            spannable.append(span)
        }

        textView.text = spannable
    }
}

