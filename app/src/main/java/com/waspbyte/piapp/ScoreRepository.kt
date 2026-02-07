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

    fun getCurrentStreak(): Int {
        val query = "WITH daily AS (\n" +
                "    SELECT\n" +
                "        date(completed_at, 'unixepoch') AS day,\n" +
                "        MAX(score) AS score\n" +
                "    FROM scores\n" +
                "    GROUP BY day\n" +
                "),\n" +
                "ordered AS (\n" +
                "    SELECT\n" +
                "        day,\n" +
                "        score,\n" +
                "        ROW_NUMBER() OVER (ORDER BY day) AS rn\n" +
                "    FROM daily\n" +
                "),\n" +
                "streaks AS (\n" +
                "    -- Base case: first day\n" +
                "    SELECT\n" +
                "        rn,\n" +
                "        day,\n" +
                "        score,\n" +
                "        score AS highscore,\n" +
                "        1 AS streak\n" +
                "    FROM ordered\n" +
                "    WHERE rn = 1\n" +
                "\n" +
                "    UNION ALL\n" +
                "\n" +
                "    SELECT\n" +
                "        o.rn,\n" +
                "        o.day,\n" +
                "        o.score,\n" +
                "        MAX(s.highscore, o.score) AS highscore,\n" +
                "        CASE\n" +
                "            -- Played but did NOT beat all-time high - streak = 0\n" +
                "            WHEN o.score <= s.highscore THEN 0\n" +
                "\n" +
                "            -- Beat high score, but missed at least one day - streak = 1\n" +
                "            WHEN julianday(o.day) - julianday(s.day) > 1 THEN 1\n" +
                "\n" +
                "            -- Beat high score on consecutive day - streak continues\n" +
                "            ELSE s.streak + 1\n" +
                "        END AS streak\n" +
                "    FROM streaks s\n" +
                "    JOIN ordered o\n" +
                "      ON o.rn = s.rn + 1\n" +
                ")\n" +
                "SELECT streak AS current_streak\n" +
                "FROM streaks\n" +
                "ORDER BY rn DESC\n" +
                "LIMIT 1;"

        val db = dbHelper.readableDatabase
        var cursor = db?.rawQuery("SELECT 1 from scores WHERE date(completed_at, 'unixepoch') = date(?, 'unixepoch')", arrayOf("${System.currentTimeMillis() / 1000L}"))
        if (!(cursor?.moveToFirst() ?: false)) {
            cursor?.close()
            return 0
        }
        cursor.close()

        cursor = db?.rawQuery(query, arrayOf())
        cursor?.moveToFirst()
        val streak = cursor?.getInt(0)
        cursor?.close()

        return streak ?: 0
    }

}