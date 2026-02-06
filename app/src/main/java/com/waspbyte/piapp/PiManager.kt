package com.waspbyte.piapp

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.min

class PiManager(context: Context) {

    var PI: String

    init {
        val inputStream = context.resources.openRawResource(R.raw.pi)
        val reader = BufferedReader(InputStreamReader(inputStream))
        PI = reader.readLine()
        reader.close()
    }
    val DOT = 1
    private var currentIndex = -1
    private var currentText = StringBuilder()
    private var visibleIndex = 0
    private var wrongAttempts = mutableListOf<Int>()
    private var isCorrect = mutableListOf<Boolean>()

    fun piWithDot() = "${PI.slice(0..<DOT)}.${PI.slice(DOT..<PI.length)}"

    fun next(c: Char, textView: TextView, maxTextWidth: Int): Char? {
        currentIndex++
        if (wrongAttempts.size <= currentIndex) {
            wrongAttempts.add(0)
            isCorrect.add(false)
        }

        currentText.append(c)
        val paint = textView.paint
        while (paint.measureText(getText()) > maxTextWidth && visibleIndex <= currentIndex) {
            visibleIndex++
        }

        return PI[currentIndex]
    }

    fun check(answer: Char) {
        val correct = answer == PI[currentIndex]
        if (!correct) {
            wrongAttempts[currentIndex]++
        }
        isCorrect[currentIndex] = correct
    }

    fun back() {
        if (currentIndex == -1) return
        isCorrect[currentIndex] = false
        currentText.deleteAt(currentText.length - 1)
        currentIndex--
        if (visibleIndex > 0) visibleIndex--
    }

    fun getScore(): Float {
        val initialPoints = (currentIndex + 1) * 5
        var points = initialPoints

        for ((correct, attempts) in isCorrect.zip(wrongAttempts)) {
            points -= if (correct) {
                min(attempts, 5)
            } else {
                5
            }
        }

        if (initialPoints == 0)
            return 1.0f

        return points.toFloat() / initialPoints.toFloat()
    }

    fun getCurrentIndex(): Int {
        return currentIndex
    }

    fun getText(): String {
        if (currentIndex < 0) return ""
        val start = visibleIndex
        val end = currentIndex
        val sb = StringBuilder()
        for (i in start..end) {
            sb.append(currentText[i])
            if (i == DOT - 1) sb.append('.')
        }
        return sb.toString()
    }

    fun getColors(): List<Int> {
        if (currentIndex < 0) return emptyList()
        val colors = ArrayList<Int>()
        val start = visibleIndex
        val end = currentIndex
        for (i in start..end) {
            colors.add(if (isCorrect[i]) Color.BLACK else Color.RED)
            if (i == DOT - 1) colors.add(Color.BLACK)
        }
        return colors
    }
}