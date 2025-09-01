package com.waspbyte.piapp

import PiManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class GameActivity : AppCompatActivity() {
    private lateinit var piManager: PiManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        piManager = PiManager()

        val digitsEt = findViewById<EditText>(R.id.digits_et)
        val pastDigitsTv = findViewById<TextView>(R.id.past_digits_tv)
        digitsEt.requestFocus()

        digitsEt.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                val back = piManager.back() ?: return@setOnKeyListener false
                if (back == '.') {
                    pastDigitsTv.text = pastDigitsTv.text.toString().substring(0, pastDigitsTv.text.length - 1)
                    piManager.back()
                }
                pastDigitsTv.text = pastDigitsTv.text.toString().substring(0, pastDigitsTv.text.length - 1)
            }
            false
        }

        digitsEt.addTextChangedListener(object : TextWatcher {
            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isEmpty()) return

                var correct = piManager.next()
                if (correct == '.') {
                    correct = piManager.next()
                    pastDigitsTv.text = pastDigitsTv.text.toString() + '.'
                }
                if (correct != s[0]) println("INCORRECT!")
                pastDigitsTv.text = pastDigitsTv.text.toString() + s

                digitsEt.text.clear()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
    }
}