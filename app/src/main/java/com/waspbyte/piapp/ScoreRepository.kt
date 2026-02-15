package com.waspbyte.piapp

import android.content.ContentValues
import android.content.Context
import kotlinx.datetime.LocalDate

class ScoreRepository(context: Context) {
    private var dbHelper: DBHelper = DBHelper(context)

    fun saveAttempt(score: Int, accuracy: Float) {
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DBHelper.ScoresEntry.COLUMN_NAME_COMPLETED_AT, System.currentTimeMillis() / 1000L)
            put(DBHelper.ScoresEntry.COLUMN_NAME_SCORE, score)
            put(DBHelper.ScoresEntry.COLUMN_NAME_ACCURACY, accuracy)
        }

        db?.insert(DBHelper.ScoresEntry.TABLE_NAME, null, values)
    }


    private val streak_query = """
            WITH daily AS (
                SELECT
                    date(completed_at, 'unixepoch') AS day,
                    MAX(score) AS score,
                    accuracy
                FROM scores
                WHERE accuracy >= 0.9
                GROUP BY day
            ),
            ordered AS (
                SELECT
                    d1.day,
                    d1.score,
                    (
                        SELECT COUNT(*)
                        FROM daily d2
                        WHERE d2.day <= d1.day
                    ) AS rn
                FROM daily d1
            ),
            streaks AS (
                -- Base case: first day
                SELECT
                    rn,
                    day,
                    score,
                    score AS highscore,
                    1 AS streak
                FROM ordered
                WHERE rn = 1

                UNION ALL

                SELECT
                    o.rn,
                    o.day,
                    o.score,
                    MAX(s.highscore, o.score) AS highscore,
                    CASE
                        -- Played but did NOT beat all-time high - streak = 0
                        WHEN o.score <= s.highscore THEN 0

                        -- Beat high score, but missed at least one day - streak = 1
                        WHEN julianday(o.day) - julianday(s.day) > 1 THEN 1

                        -- Beat high score on consecutive day - streak continues
                        ELSE s.streak + 1
                    END AS streak
                FROM streaks s
                JOIN ordered o
                  ON o.rn = s.rn + 1
            )
        """.trimIndent()

    fun getCurrentStreak(): Int {
        val db = dbHelper.readableDatabase
        var cursor = db?.rawQuery("SELECT 1 from scores WHERE date(completed_at, 'unixepoch') = date(?, 'unixepoch')", arrayOf("${System.currentTimeMillis() / 1000L}"))
        if (!(cursor?.moveToFirst() ?: false)) {
            cursor?.close()
            return 0
        }
        cursor.close()

        val querySuffix = """
            SELECT streak AS current_streak
            FROM streaks
            ORDER BY rn DESC
            LIMIT 1
        """.trimIndent()

        cursor = db?.rawQuery("$streak_query\n$querySuffix", arrayOf())
        cursor?.moveToFirst()
        val streak = cursor?.getInt(0)
        cursor?.close()

        return streak ?: 0
    }

    fun getBestStreak(): Int {
        val db = dbHelper.readableDatabase

        val querySuffix = """
            SELECT MAX(streak)
            FROM streaks
        """.trimIndent()

        val cursor = db?.rawQuery("$streak_query\n$querySuffix", arrayOf())
        cursor?.moveToFirst()
        val streak = cursor?.getInt(0)
        cursor?.close()

        return streak ?: 0
    }

    fun getCurrentIndex(): Int {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT MAX(score)
            FROM scores
            WHERE accuracy >= 0.9
        """.trimIndent()

        val cursor = db?.rawQuery(query, arrayOf())
        cursor?.moveToFirst()
        val score = cursor?.getInt(0)
        cursor?.close()

        return score ?: 0
    }

    fun getHighScores(fromDate: Long): List<Pair<String, Float>> {
        val db = dbHelper.readableDatabase

        val query = """
            WITH daily AS (
                SELECT
                    date(completed_at, 'unixepoch') AS day,
                    MAX(score) AS score,
                    accuracy
                FROM scores
                WHERE accuracy >= 0.9
                GROUP BY day
            ),
            ordered AS (
              SELECT d.day, d.score,
                (SELECT COUNT(*) FROM daily d2 WHERE d2.day <= d.day) AS rn
              FROM daily d
            ),
            highscores AS (
                SELECT
                    rn,
                    day,
                    score,
                    score AS highscore
                FROM ordered
                WHERE rn = 1

                UNION ALL

                SELECT
                    o.rn,
                    o.day,
                    o.score,
                    MAX(h.highscore, o.score) AS highscore
                FROM highscores h
                JOIN ordered o
                  ON o.rn = h.rn + 1
            ),
            bounds AS (
                SELECT MIN(day) AS min_day, MAX(day) AS max_day FROM highscores
            ),
            calendar(day) AS (
                SELECT min_day FROM bounds
                UNION ALL
                SELECT date(day, '+1 day') FROM calendar, bounds WHERE day < bounds.max_day
            ),
            calendar_values AS (
                SELECT c.day,
                       hs.highscore
                FROM calendar c
                LEFT JOIN highscores hs ON hs.day = c.day
            ),
            result AS (
              SELECT
                cv.day,
                COALESCE(
                  cv.highscore,
                  (
                    SELECT MAX(cv2.highscore)
                    FROM calendar_values cv2
                    WHERE cv2.day < cv.day
                  )
                ) AS filled_highscore
              FROM calendar_values cv
            )
            SELECT * FROM result
            WHERE day > date($fromDate, 'unixepoch')
        """.trimIndent()

        val highScores = mutableListOf<Pair<String, Float>>()
        val cursor = db?.rawQuery(query, arrayOf())
        while (cursor?.moveToNext() == true) {
            val date = cursor.getString(0)
            val highScore = cursor.getInt(1)
            highScores.add(Pair(date, highScore.toFloat()))
        }
        cursor?.close()

        return highScores.toList()
    }

    fun getHeatmap(): HeatmapData {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT date(completed_at, 'unixepoch') AS day, completed_at, COUNT(*) AS cnt
            FROM scores
            GROUP BY day
            ORDER BY day;
        """.trimIndent()

        val heatmapData = mutableMapOf<LocalDate, Float>()

        val cursor = db?.rawQuery(query, arrayOf())
        while (cursor?.moveToNext() == true) {
            val date = cursor.getLong(1)
            val count = cursor.getInt(2)
            heatmapData[LocalDate.fromEpochDays(date/60/60/24)] = when (count) {
                in 5..Integer.MAX_VALUE -> 1.0f
                4 -> 0.8f
                3 -> 0.6f
                2 -> 0.5f
                1 -> 0.3f
                0 -> 0.0f
                else -> 0.0f
            }
        }
        cursor?.close()

        return heatmapData
    }
}