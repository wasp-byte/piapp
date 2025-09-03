package com.waspbyte.piapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val textView: TextView
    private val editText: EditText

    init {
        LayoutInflater.from(context).inflate(R.layout.game_view, this, true)
        textView = findViewById<TextView>(R.id.past_digits_tv)
        editText = findViewById<EditText>(R.id.digits_et)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        val textViewWidth = textView.measuredWidth + paddingLeft
        val textViewHeight = textView.measuredHeight
        val editTextWidth = editText.measuredWidth
        val editTextHeight = editText.measuredHeight

        if (textViewWidth < (width - editTextWidth ) / 2) {
            textView.layout((width - editTextWidth) / 2 - textViewWidth, 0, (width - editTextWidth) / 2, textViewHeight)
            textView.measure(0, 0)
            editText.layout((width - editTextWidth) / 2, 0, (width + editTextWidth) / 2, editTextHeight)
        } else {
            textView.layout(paddingLeft, 0, textViewWidth, textViewHeight)
            textView.measure(0, 0)
            editText.layout(textViewWidth, 0, textViewWidth + editTextWidth, editTextHeight)
        }
    }
}