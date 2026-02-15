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
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndScreenTest {

    private lateinit var scenario: ActivityScenario<EndScreenActivity>
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var context: Context
    private val accuracy = 1.0f
    private val currentIndex = 1

    @Before
    fun setUp() {
        Intents.init()
        context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, EndScreenActivity::class.java).apply {
            putExtra(context.getString(R.string.accuracy), accuracy)
            putExtra(context.getString(R.string.current_index), currentIndex)
        }

        scenario = launch(intent)
        scoreRepository = ScoreRepository(context)
    }

    @After
    fun cleanUp() {
        Intents.release()
    }

    @Test
    fun checkOpen_EndScreen() {
        intended(hasComponent(EndScreenActivity::class.java.getName()))
    }

    @Test
    fun clickBack_openMainActivity() {
        onView(withId(R.id.back_btn)).perform(click())
        scenario.onActivity { activity ->
            assert(activity.isFinishing) { "Expected activity to finish after click" }
        }
    }

    @Test
    fun clickAgain_openGameActivity() {
        onView(withId(R.id.again_btn)).perform(click())
        intended(hasComponent(GameActivity::class.java.getName()))
    }

    @Test
    fun checkScoreRepository() {
        val scoreRepositoryCurrentStreak = scoreRepository.getCurrentStreak()
        assert(scoreRepositoryCurrentStreak == 1) { "Expected streak 1 get $scoreRepositoryCurrentStreak" }
        val scoreRepositoryCurrentIndex = scoreRepository.getCurrentIndex()
        assert(scoreRepositoryCurrentIndex == currentIndex) { "Expected currentIndex $currentIndex get $scoreRepositoryCurrentIndex" }
        val scoreRepositoryCurrentIndexFromHistory = scoreRepository.getHighScores(0).first().second
        assert(
            scoreRepositoryCurrentIndexFromHistory == currentIndex.toFloat()
        ) { "Expected currentIndex $currentIndex get $scoreRepositoryCurrentIndexFromHistory" }
    }

    @Test
    fun checkIndexTv() {
        onView(withId(R.id.index_tv)).check(matches(withText(currentIndex.toString())))
    }

    @Test
    fun checkAccuracyTv() {
        onView(withId(R.id.accuracy_tv)).check(
            matches(
                withText(
                    context.getString(R.string.format_accuracy).format(accuracy * 100f)
                )
            )
        )
    }
}