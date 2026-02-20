package com.waspbyte.piapp

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.toSpannable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.updatePadding

class GameActivity : AppCompatActivity() {
    internal lateinit var piManager: PiManager
    private lateinit var digitsTv: TextView

    private var maxTextWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        digitsTv = findViewById(R.id.digits_tv)

        val backBtn = findViewById<Button>(R.id.back_btn)
        ViewCompat.setOnApplyWindowInsetsListener(backBtn) { v, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            v.updatePadding(top = statusBarHeight)
            insets
        }
        backBtn.setOnClickListener {
            finish()
        }

        val width = Resources.getSystem().displayMetrics.widthPixels
        maxTextWidth = width - digitsTv.marginStart - digitsTv.marginEnd
        piManager = createPiManager()
    }

    fun createPiManager() = PiManager(this)

    fun addText(view: View) {
        piManager.next((view as Button).text[0], digitsTv.paint, maxTextWidth)
        piManager.check(view.text[0])
        formatText()
    }

    // Unused parameter, method is used as a callback for onClick
    fun delete(view: View) {
        piManager.back()
        formatText()
    }

    // Unused parameter, method is used as a callback for onClick
    fun done(view: View) {
        val intent = Intent(this, EndScreenActivity::class.java)
        intent.putExtra(getString(R.string.accuracy), piManager.getScore())
        intent.putExtra(getString(R.string.current_index), piManager.getCurrentIndex() + 1)
        startActivity(intent)
        finish()
    }

    private fun buildSpannableText(
        text: String,
        colors: List<Int>,
        errorColor: Int
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()

        text.forEachIndexed { i, char ->
            val span = SpannableString(char.toString())
            if (colors[i] != Color.BLACK) {
                span.setSpan(
                    ForegroundColorSpan(errorColor),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            builder.append(span)
        }

        return builder
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

        val builder = buildSpannableText(text, colors, typedValue.data)
        digitsTv.text = builder.toSpannable()
    }

    override fun onPause() {
        super.onPause()
        piManager = PiManager(this)
        digitsTv.text = ""
    }
}
