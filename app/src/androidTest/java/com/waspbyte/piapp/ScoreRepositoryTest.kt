package com.waspbyte.piapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Collections.emptyList

@RunWith(AndroidJUnit4::class)
class ScoreRepositoryTest {
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var context: Context

    data class Attempts(
        val score: Int,
        val accuracy: Float,
        val year: Int,
        val month: Int,
        val day: Int,
        val hour: Int,
        val minute: Int,
        val second: Int
    ) {
        val epoch: Long
            get() = LocalDateTime(year, month, day, hour, minute, second).toJavaLocalDateTime()
                .toEpochSecond(
                    ZoneOffset.UTC
                )
    }

    private val attempts = listOf(
        Attempts(11, 0.9001f, 2025, 5, 1, 13, 5, 6), // streak = 1
        Attempts(12, 0.9812f, 2025, 5, 2, 19, 45, 8), // streak = 2
        Attempts(28, 0.2106f, 2025, 5, 4, 10, 11, 3), // streak = 0
        Attempts(0, 1.0f, 2025, 5, 6, 0, 5, 42), // streak = 0
        Attempts(76, 0.889f, 2025, 5, 9, 21, 47, 55), // streak = 0
        Attempts(25, 0.921f, 2025, 5, 11, 7, 3, 54), // streak = 1
        Attempts(33, 0.99f, 2025, 5, 13, 18, 36, 44), // streak = 2
        Attempts(87, 0.9f, 2025, 5, 14, 4, 12, 37), // streak = 3
        Attempts(88, 0.5577f, 2025, 5, 15, 15, 18, 29), // streak = 0
        Attempts(5, 0.9951f, 2025, 5, 19, 9, 27, 12), // streak = 0
        Attempts(92, 0.3333f, 2025, 5, 21, 6, 40, 18), // streak = 0
        Attempts(100, 0.95f, 2025, 5, 22, 12, 30, 16), // streak = 1
        Attempts(105, 0.95f, 2025, 5, 22, 12, 30, 16), // streak = 1
        Attempts(65, 0.4344f, 2025, 5, 25, 2, 54, 20), // streak = 0
        Attempts(54, 0.1073f, 2025, 5, 29, 23, 59, 1), // streak = 0
        Attempts(105, 0.91f, 2025, 5, 30, 23, 59, 59), // streak = 1
    )

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        scoreRepository = ScoreRepository(context)
        context.deleteDatabase(DBHelper.DATABASE_NAME)
        for (attempt in attempts) {
            scoreRepository.saveAttempt(
                attempt.score, attempt.accuracy, attempt.epoch
            )
        }
    }

    @After
    fun teardown() {
        scoreRepository.close()
        context.deleteDatabase(DBHelper.DATABASE_NAME)
    }

    @Test
    fun getBestStreak() {
        var bestStreak = 0
        var streak = 0
        var previousDay = 0
        for (attempt in attempts) {
            val day = -LocalDate(attempt.year, attempt.month, attempt.day).daysUntil(
                LocalDate(
                    1970,
                    1,
                    1
                )
            )
            if (day - previousDay == 1) {
                streak++
            } else {
                if (streak > bestStreak)
                    bestStreak = streak
                streak = 0
            }
            previousDay = day
        }
        assertEquals(bestStreak, scoreRepository.getBestStreak())
    }

    @Test
    fun getCurrentIndex() {
        var currentIndex = 0
        for (attempt in attempts) {
            if (attempt.accuracy >= 0.9f && attempt.score > currentIndex)
                currentIndex = attempt.score
        }
        assertEquals(currentIndex, scoreRepository.getCurrentIndex())
    }

    fun getHighScores(fromDate: Long): List<Pair<String, Float>> {
        val filteredAttempts = attempts.filter { it.accuracy >= 0.9 }

        val dailyScores =
            filteredAttempts.groupBy { java.time.LocalDate.of(it.year, it.month, it.day) }
                .mapValues { (_, scores) -> scores.maxOfOrNull { it.score } ?: 0 }

        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
        val minDate = dailyScores.keys.minOrNull()
        val maxDate = dailyScores.keys.maxOrNull()

        if (minDate == null || maxDate == null) return emptyList()

        val filledScores = mutableMapOf<java.time.LocalDate, Float>()
        var lastHighScore = 0f

        for (date in minDate.toEpochDay()..maxDate.toEpochDay()) {
            val currentDate = java.time.LocalDate.ofEpochDay(date)
            val score = dailyScores[currentDate] ?: lastHighScore
            filledScores[currentDate] = score.toFloat()

            if (score.toFloat() > lastHighScore) {
                lastHighScore = score.toFloat()
            }

            if (lastHighScore > filledScores.getOrDefault(currentDate, 0f)) {
                filledScores[currentDate] = lastHighScore
            }
        }

        return filledScores.filterKeys {
            it.atStartOfDay().toEpochSecond(ZoneOffset.UTC) > fromDate
        }.map { it.key.format(dateFormatter) to it.value }
    }

    @Test
    fun getHighScoresAll() {
        val all = 0L
        val highScoresAll = getHighScores(all)
        assertEquals(highScoresAll, scoreRepository.getHighScores(all))
    }

    @Test
    fun getHighScoresYear() {
        val year = attempts.last().epoch - 60 * 60 * 24 * 365
        val highScoresYear = getHighScores(year)
        assertEquals(highScoresYear, scoreRepository.getHighScores(year))
    }

    @Test
    fun getHighScoresMonth() {
        val month = attempts.last().epoch - 60 * 60 * 24 * 30
        val highScoresMonth = getHighScores(month)
        assertEquals(highScoresMonth, scoreRepository.getHighScores(month))
    }

    @Test
    fun getHighScoresWeek() {
        val week = attempts.last().epoch - 60 * 60 * 24 * 7
        val highScoresWeek = getHighScores(week)
        assertEquals(highScoresWeek, scoreRepository.getHighScores(week))
    }

    @Test
    fun getHeatmap() {
        val counts = attempts.groupBy { java.time.LocalDate.of(it.year, it.month, it.day) }
            .mapValues { it.value.size }

        val heatmapData = mutableMapOf<LocalDate, Float>()
        counts.forEach { (date, count) ->
            heatmapData[date.toKotlinLocalDate()] = when (count) {
                in 5..Int.MAX_VALUE -> 1.0f
                4 -> 0.8f
                3 -> 0.6f
                2 -> 0.5f
                1 -> 0.3f
                0 -> 0.0f
                else -> 0.0f
            }
        }

        assertEquals(heatmapData, scoreRepository.getHeatmap())
    }
}