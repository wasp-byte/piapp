package com.waspbyte.piapp

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityTest {
    private lateinit var scenario: ActivityScenario<GameActivity>

    @Before
    fun setup() {
        scenario = ActivityScenario.launch(GameActivity::class.java)
    }

    @After
    fun teardown() {
        scenario.close()
    }

    @Test
    fun addText_updatesTextView() {
        scenario.onActivity { activity ->
            val button = Button(activity).apply {
                text = "3"
            }

            activity.addText(button)

            val textView = activity.findViewById<TextView>(R.id.digits_tv)
            assertTrue(textView.text.isNotEmpty())
        }
    }

    @Test
    fun delete_removesLastDigit() {
        scenario.onActivity { activity ->
            val button = Button(activity).apply { text = "3" }
            activity.addText(button)

            activity.delete()

            val textView = activity.findViewById<TextView>(R.id.digits_tv)
            assertEquals("", textView.text.toString())
        }
    }

    @Test
    fun done_startsEndScreenActivity_withExtras() {
        Intents.init()

        scenario.onActivity { activity ->
            activity.done()
        }

        intended(hasComponent(EndScreenActivity::class.java.name))
        val context = ApplicationProvider.getApplicationContext<Context>()
        intended(hasExtra(context.getString(R.string.current_index), 0))

        Intents.release()
    }

    @Test
    fun multipleDigits_renderCorrectLength() {
        scenario.onActivity { activity ->
            val btn = Button(activity)

            btn.text = "3"
            activity.addText(btn)

            btn.text = "1"
            activity.addText(btn)

            val tv = activity.findViewById<TextView>(R.id.digits_tv)
            assertTrue(tv.text.length >= 2)
        }
    }
}

