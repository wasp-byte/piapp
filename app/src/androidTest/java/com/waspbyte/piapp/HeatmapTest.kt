package com.waspbyte.piapp

import android.util.Log
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeatmapTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<StatsActivity>()

    @Test
    fun heatmap_title_day_and_days_play_present() {
        val endDate = currentDateTime().date

        val sampleData = mutableMapOf<LocalDate, Float>()
        sampleData[endDate] = 0.6f

        composeRule.activityRule.scenario.onActivity { activity ->
            val composeView =
                activity.findViewById<androidx.compose.ui.platform.ComposeView>(R.id.heatmap_cv)
            composeView.setContent {
                HeatmapSection(
                    firstDayOfWeek = DayOfWeek.MONDAY,
                    data = sampleData,
                )
            }
        }

        composeRule.onNodeWithText("Heatmap").assertIsDisplayed()

        composeRule.onAllNodesWithTag("played").assertCountEquals(sampleData.size)
    }
}