package com.waspbyte.piapp

import android.content.ContentValues
import android.content.Context

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
                    MAX(score) AS score
                FROM scores
                GROUP BY day
            ),
            ordered AS (
                SELECT
                    day,
                    score,
                    ROW_NUMBER() OVER (ORDER BY day) AS rn
                FROM daily
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
        """.trimIndent()

        val cursor = db?.rawQuery(query, arrayOf())
        cursor?.moveToFirst()
        val score = cursor?.getInt(0)
        cursor?.close()

        return score ?: 0
    }

}