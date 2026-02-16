package com.waspbyte.piapp

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsEmptyTest {

    private lateinit var scenario: ActivityScenario<StatsActivity>
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        Intents.init()
        context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, StatsActivity::class.java)

        scenario = launch(intent)
        scoreRepository = ScoreRepository(context)
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun checkOpen_StatsActivity() {
        intended(hasComponent(StatsActivity::class.java.getName()))
    }

    @Test
    fun checkStreakTv() {
        onView(withId(R.id.streak_tv)).check(matches(withText("0")))
    }

    @Test
    fun checkGone() {
        onView(withId(R.id.highscore_ranges_s)).check(matches(not(isDisplayed())))
        onView(withId(R.id.highscore_tv)).check(matches(not(isDisplayed())))
        onView(withId(R.id.highscore_lcv)).check(matches(not(isDisplayed())))
    }
}