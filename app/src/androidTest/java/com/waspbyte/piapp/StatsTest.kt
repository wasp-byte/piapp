package com.waspbyte.piapp

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
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
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StatsTest {

    private lateinit var scenario: ActivityScenario<StatsActivity>
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var context: Context

    data class Attempts(
        val score: Int,
        val accuracy: Float,
        val epoch: Long,
    )

    private val attempts = listOf(
        Attempts(11, 0.9001f, System.currentTimeMillis() / 1000L - 60 * 60 * 24 * 2), // streak = 1
        Attempts(12, 0.9812f, System.currentTimeMillis() / 1000L - 60 * 60 * 24 * 1), // streak = 2
        Attempts(28, 0.9123f, System.currentTimeMillis() / 1000L), // streak = 3
    )

    @Before
    fun setUp() {
        Intents.init()
        context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, StatsActivity::class.java)

        scoreRepository = ScoreRepository(context)
        for (attempt in attempts) {
            scoreRepository.saveAttempt(
                attempt.score, attempt.accuracy, attempt.epoch
            )
        }
        scenario = launch(intent)
    }

    @After
    fun cleanUp() {
        Intents.release()
        scenario.close()
        context.deleteDatabase(DBHelper.DATABASE_NAME)
    }

    @Test
    fun checkOpen_StatsActivity() {
        intended(hasComponent(StatsActivity::class.java.getName()))
    }

    @Test
    fun clickBack_openMainActivity() {
        onView(withId(R.id.back_btn)).perform(click())
        scenario.onActivity { activity ->
            assertTrue("Expected activity to finish after click", activity.isFinishing)
        }
    }

    @Test
    fun checkStreakTv() {
        onView(withId(R.id.streak_tv)).check(matches(withText(scoreRepository.getCurrentStreak().toString())))
    }

    @Test
    fun checkHighScoreSpinner() {
        onView(withId(R.id.highscore_ranges_s)).perform(click());
        onView(withText(context.getString(R.string.all))).perform(click());
        onView(withId(R.id.highscore_ranges_s)).perform(click());
        onView(withText(context.getString(R.string.year))).perform(click());
        onView(withId(R.id.highscore_ranges_s)).perform(click());
        onView(withText(context.getString(R.string.month))).perform(click());
        onView(withId(R.id.highscore_ranges_s)).perform(click());
        onView(withText(context.getString(R.string.week))).perform(click());
    }
}
