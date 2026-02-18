package com.waspbyte.piapp

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PiManagerTest {
    private lateinit var context: Context
    private lateinit var piManager: PiManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        piManager = PiManager(context)
    }

    @Test
    fun checkDot() {
        assertEquals('.', piManager.piWithDot()[1])
    }

    @Test
    fun checkNext() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        piManager.next('3', textView.paint, 1000)
        assertEquals(0, piManager.getCurrentIndex())
        assertTrue(piManager.check('3'))
        assertEquals("3.", piManager.getText())
        piManager.next('1', textView.paint, 1000)
        assertEquals(1, piManager.getCurrentIndex())
        assertTrue(piManager.check('1'))
        assertEquals("3.1", piManager.getText())
        piManager.next('2', textView.paint, 1000)
        assertEquals(2, piManager.getCurrentIndex())
        assertFalse(piManager.check('2'))
        assertEquals("3.12", piManager.getText())
    }

    @Test
    fun checkBack() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        piManager.next('3', textView.paint, 1000)
        assertEquals(0, piManager.getCurrentIndex())
        assertTrue(piManager.check('3'))
        assertEquals("3.", piManager.getText())
        piManager.next('1', textView.paint, 1000)
        assertEquals(1, piManager.getCurrentIndex())
        assertTrue(piManager.check('1'))
        assertEquals("3.1", piManager.getText())
        piManager.next('2', textView.paint, 1000)
        assertEquals(2, piManager.getCurrentIndex())
        assertFalse(piManager.check('2'))
        assertEquals("3.12", piManager.getText())
        piManager.back()
        assertEquals(1, piManager.getCurrentIndex())
        assertEquals("3.1", piManager.getText())
        piManager.back()
        assertEquals(0, piManager.getCurrentIndex())
        assertEquals("3.", piManager.getText())
        piManager.back()
        assertEquals(-1, piManager.getCurrentIndex())
        assertEquals("", piManager.getText())
    }

    @Test
    fun checkScore() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        assertEquals(1.0f, piManager.getScore())
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assertEquals(1.0f, piManager.getScore())
        piManager.next('1', textView.paint, 1000)
        piManager.check('1')
        assertEquals(1.0f, piManager.getScore())
    }

    @Test
    fun checkIndex() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        for (i in 1..100) {
            piManager.next('1', textView.paint, 1000)
            piManager.check('1')
            assertEquals(-1 + i, piManager.getCurrentIndex())
        }
    }

    @Test
    fun test_check() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        val pi = "3141592"
        for (i in 0 until pi.length) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            assertEquals(1.0f, piManager.getScore())
        }
        val notPi = "0000000"
        for (i in 0 until notPi.length) {
            piManager.next(notPi[i], textView.paint, 1000)
            piManager.check(notPi[i])
            assertEquals(0.0f, piManager.getScore())
        }
    }

    @Test
    fun check_getText() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val pi = "3141"
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assertEquals("3.", piManager.getText())
        var now = "3."
        for (i in 1 until pi.length) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            assertEquals(now, piManager.getText())
        }
    }

    @Test
    fun check_getText_with_more_numbers() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val pi = piManager.PI
        val charWidth = textView.paint.measureText("0")
        val maxCharsNumber = (1000 / charWidth).toInt()
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assertEquals("3.", piManager.getText())
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            assertEquals(now, piManager.getText())
        }
        piManager.next(pi[maxCharsNumber - 1], textView.paint, 1000)
        piManager.check(pi[maxCharsNumber - 1])
        now = now.slice(1 until now.length)
        now += pi[maxCharsNumber - 1]
        for (i in 1 until 20) {
            piManager.next(pi[maxCharsNumber - 1 + i], textView.paint, 1000)
            piManager.check(pi[maxCharsNumber - 1 + i])
            now = now.slice(1 until now.length)
            now += pi[maxCharsNumber - 1 + i]
            assertEquals(now, piManager.getText())
        }
    }

    @Test
    fun check_getColors_all_black() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val pi = piManager.PI
        val charWidth = textView.paint.measureText("0")
        val maxCharsNumber = (1000 / charWidth).toInt()
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assertEquals("3.", piManager.getText())
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            assertEquals(List(now.length) { index ->
                Color.BLACK
            }, piManager.getColors())
        }
        piManager.next(pi[maxCharsNumber - 1], textView.paint, 1000)
        piManager.check(pi[maxCharsNumber - 1])
        now = now.slice(1 until now.length)
        now += pi[maxCharsNumber - 1]
        for (i in 1 until 20) {
            piManager.next(pi[maxCharsNumber - 1 + i], textView.paint, 1000)
            piManager.check(pi[maxCharsNumber - 1 + i])
            now = now.slice(1 until now.length)
            now += pi[maxCharsNumber - 1 + i]
            assertEquals(List(now.length) { index ->
                Color.BLACK
            }, piManager.getColors())
        }
    }

    @Test
    fun check_getColors() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val pi = piManager.PI
        val maxCharsNumber = 7
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assertEquals("3.", piManager.getText())
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            val x = if (i % 2 == 0) '0'
            else pi[i]
            piManager.next(x, textView.paint, 1000)
            piManager.check(x)
            now += x
            assertEquals(List(now.length) { index ->
                if (index < 2) Color.BLACK
                else if (index % 2 == 0) Color.BLACK
                else Color.RED
            }, piManager.getColors())
        }
        piManager.next('0', textView.paint, 1000)
        piManager.check('0')
        now = now.slice(1 until now.length)
        now += '0'
        for (i in 1 until 20) {
            val x = if (i % 2 == 0) '0'
            else pi[maxCharsNumber - 1 + i]
            piManager.next(x, textView.paint, 1000)
            piManager.check(x)
            now = now.slice(1 until now.length)
            now += x
            assertEquals(List(now.length) { index ->
                if (i % 2 == 0) {
                    if (index % 2 == 0) Color.RED
                    else Color.BLACK
                } else {
                    if (index % 2 == 0) Color.BLACK
                    else Color.RED
                }
            }, piManager.getColors())
        }
    }
}