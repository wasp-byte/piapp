package com.waspbyte.piapp

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = ScoreRepository(context)
        repo.saveAttempt(42, 0.95f)
    }

    @Test
    fun highscore_isDisplayed() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.highscore_tv)).check(matches(withText("42")))
    }

    @Test
    fun toGameButton_startsGameActivity() {
        Intents.init()

        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.to_game_btn)).perform(click())

        intended(hasComponent(GameActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun streakButton_startsStatsActivity() {
        Intents.init()

        ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.streak_btn)).perform(click())

        intended(hasComponent(StatsActivity::class.java.name))

        Intents.release()
    }

    @Test
    fun onRestart_updatesHighscore() {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        scenario.onActivity { activity ->
            val repo = ScoreRepository(activity)
            repo.saveAttempt(69, 1.0f)
            activity.onRestart()
        }

        onView(withId(R.id.highscore_tv)).check(matches(withText("69")))
    }
}

