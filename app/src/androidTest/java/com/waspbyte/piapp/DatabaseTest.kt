package com.waspbyte.piapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBHelperTest {
    private lateinit var dbHelper: DBHelper
    private lateinit var db: SQLiteDatabase
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        context.deleteDatabase(DBHelper.DATABASE_NAME)
        dbHelper = DBHelper(context)
        db = dbHelper.writableDatabase
    }

    @After
    fun tearDown() {
        db.close()
        dbHelper.close()
        context.deleteDatabase(DBHelper.DATABASE_NAME)
    }

    @Test
    fun database_isCreated_withScoresTable() {
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(DBHelper.ScoresEntry.TABLE_NAME)
        )

        assertTrue(cursor.moveToFirst())
        cursor.close()
    }

    @Test
    fun scoresTable_hasCorrectColumns() {
        val cursor = db.rawQuery(
            "PRAGMA table_info(${DBHelper.ScoresEntry.TABLE_NAME})",
            null
        )

        val columns = mutableListOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()

        assertTrue(columns.contains(BaseColumns._ID))
        assertTrue(columns.contains(DBHelper.ScoresEntry.COLUMN_NAME_COMPLETED_AT))
        assertTrue(columns.contains(DBHelper.ScoresEntry.COLUMN_NAME_SCORE))
        assertTrue(columns.contains(DBHelper.ScoresEntry.COLUMN_NAME_ACCURACY))
    }

    @Test
    fun insert_andRead_scoreWorks() {
        val values = ContentValues().apply {
            put(DBHelper.ScoresEntry.COLUMN_NAME_COMPLETED_AT, 123456789L)
            put(DBHelper.ScoresEntry.COLUMN_NAME_SCORE, 100)
            put(DBHelper.ScoresEntry.COLUMN_NAME_ACCURACY, 0.95)
        }

        val id = db.insert(DBHelper.ScoresEntry.TABLE_NAME, null, values)
        assertTrue(id != -1L)

        val cursor = db.query(
            DBHelper.ScoresEntry.TABLE_NAME,
            null,
            "${BaseColumns._ID}=?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        assertTrue(cursor.moveToFirst())
        assertEquals(100, cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ScoresEntry.COLUMN_NAME_SCORE)))
        cursor.close()
    }
}

