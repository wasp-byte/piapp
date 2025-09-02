package com.waspbyte.piapp

import PiManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
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

        val gameView = findViewById<GameView>(R.id.game_view)
        val digitsEt = gameView.findViewById<EditText>(R.id.digits_et)
        val pastDigitsTv = gameView.findViewById<TextView>(R.id.past_digits_tv)
        digitsEt.requestFocus()

        digitsEt.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                if (piManager.isDot()) {
                    pastDigitsTv.text = pastDigitsTv.text.toString().substring(0, pastDigitsTv.text.length - 1)
                }
                piManager.back()
                if (!pastDigitsTv.text.isEmpty()) {
                    pastDigitsTv.text = pastDigitsTv.text.toString().substring(0, pastDigitsTv.text.length - 1)
                    formatText(piManager.getColors(), pastDigitsTv)
                }
            }
            false
        }

        digitsEt.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) return
                if (!s.isDigitsOnly()) {
                    digitsEt.text.clear()
                    return
                }

                piManager.next()
                piManager.check(s[0])

                pastDigitsTv.text = pastDigitsTv.text.toString() + s
                if (piManager.isDot()) {
                    pastDigitsTv.text = pastDigitsTv.text.toString() + '.'
                }
                digitsEt.text.clear()

                formatText(piManager.getColors(), pastDigitsTv)
                println(piManager.getPoints())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun formatText(colors: List<Int>, textView: TextView) {
        val text = textView.text
        if (text.isEmpty()) return
        textView.text = ""
        for ((i, color) in colors.withIndex()) {
            val word = SpannableString(text[i].toString())
            word.setSpan(BackgroundColorSpan(color), 0, word.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            textView.append(word)
        }
    }
}