package com.waspbyte.piapp

import android.content.Intent
import androidx.core.content.ContextCompat.getString
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EndScreenTest {

    @Test
    fun checkOpen_EndScreen() {
        Intents.init()
        val intent = Intent(ApplicationProvider.getApplicationContext(), EndScreenActivity::class.java).putExtra(
            getString(ApplicationProvider.getApplicationContext(), R.string.accuracy), 1.0f).putExtra(getString(
            ApplicationProvider.getApplicationContext(), R.string.current_index), 1)
        launch<EndScreenActivity>(intent).use { scenario ->
            intended(hasComponent(EndScreenActivity::class.java.getName()))
        }
    }
}