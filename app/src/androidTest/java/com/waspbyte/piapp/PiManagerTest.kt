package com.waspbyte.piapp

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        assert(piManager.piWithDot()[1] == '.')
    }

    @Test
    fun checkNext() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        piManager.next('3', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 0)
        assert(piManager.check('3'))
        assert(piManager.getText() == "3.")
        piManager.next('1', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 1)
        assert(piManager.check('1'))
        assert(piManager.getText() == "3.1")
        piManager.next('2', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 2)
        assert(!piManager.check('2'))
        assert(piManager.getText() == "3.12")
    }

    @Test
    fun checkBack() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        piManager.next('3', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 0)
        assert(piManager.check('3'))
        assert(piManager.getText() == "3.")
        piManager.next('1', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 1)
        assert(piManager.check('1'))
        assert(piManager.getText() == "3.1")
        piManager.next('2', textView.paint, 1000)
        assert(piManager.getCurrentIndex() == 2)
        assert(!piManager.check('2'))
        assert(piManager.getText() == "3.12")
        piManager.back()
        assert(piManager.getCurrentIndex() == 1)
        assert(piManager.getText() == "3.1")
        piManager.back()
        assert(piManager.getCurrentIndex() == 0)
        assert(piManager.getText() == "3.")
        piManager.back()
        assert(piManager.getCurrentIndex() == -1)
        assert(piManager.getText() == "")
    }

    @Test
    fun checkScore() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        assert(piManager.getScore() == 1.0f)
        piManager.next('3', textView.paint, 1000)
        piManager.check('3')
        assert(piManager.getScore() == 1.0f)
        piManager.next('1', textView.paint, 1000)
        piManager.check('1')
        assert(piManager.getScore() == 1.0f)
    }

    @Test
    fun checkIndex() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        for (i in 1..100) {
            piManager.next('1', textView.paint, 1000)
            piManager.check('1')
            assert(piManager.getCurrentIndex() == -1 + i)
        }
    }

    @Test
    fun test_check() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            typeface = ResourcesCompat.getFont(context, R.font.overpass_mono_regular)
        }
        val pi = "3141592"
        for (i in 0 until pi.length) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            assert(piManager.getScore() == 1.0f)
        }
        val notPi = "0000000"
        for (i in 0 until notPi.length) {
            piManager.next(notPi[i], textView.paint, 1000)
            piManager.check(notPi[i])
            assert(piManager.getScore() == 0.0f)
        }
    }

    @Test
    fun check_getText() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        assert(piManager.getText() == "3.")
        var now = "3."
        for (i in 1 until pi.length) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            assert(piManager.getText() == now)
        }
    }

    @Test
    fun check_getText_with_more_numbers() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        assert(piManager.getText() == "3.")
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            assert(piManager.getText() == now)
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
            assert(piManager.getText() == now)
        }
    }

    @Test
    fun check_getColors_all_black() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        assert(piManager.getText() == "3.")
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            piManager.next(pi[i], textView.paint, 1000)
            piManager.check(pi[i])
            now += pi[i]
            Log.d("sadassfsasfdsdf", piManager.getColors().toString())
            assert(piManager.getColors() == List(now.length) { index ->
                Color.BLACK
            })
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
            assert(piManager.getColors() == List(now.length) { index ->
                Color.BLACK
            })
        }
    }

    @Test
    fun check_getColors() {
        val textView = TextView(context).apply {
            textSize = 80f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
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
        assert(piManager.getText() == "3.")
        var now = "3."
        for (i in 1 until maxCharsNumber - 1) {
            val x = if (i % 2 == 0)
                '0'
            else
                pi[i]
            piManager.next(x, textView.paint, 1000)
            piManager.check(x)
            now += x
            assert(piManager.getColors() == List(now.length) { index ->
                if (index < 2)
                    Color.BLACK
                else if (index % 2 == 0)
                    Color.BLACK
                else
                    Color.RED
            })
        }
        piManager.next('0', textView.paint, 1000)
        piManager.check('0')
        now = now.slice(1 until now.length)
        now += '0'
        for (i in 1 until 20) {
            val x = if (i % 2 == 0)
                '0'
            else
                pi[maxCharsNumber - 1 + i]
            piManager.next(x, textView.paint, 1000)
            piManager.check(x)
            now = now.slice(1 until now.length)
            now += x
            assert(piManager.getColors() == List(now.length) { index ->
                if (i % 2 == 0) {
                    if (index % 2 == 0)
                        Color.RED
                    else
                        Color.BLACK
                } else {
                    if (index % 2 == 0)
                        Color.BLACK
                    else
                        Color.RED
                }
            })
        }
    }
}