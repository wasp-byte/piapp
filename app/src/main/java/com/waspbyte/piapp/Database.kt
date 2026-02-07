package com.waspbyte.piapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    object ScoresEntry : BaseColumns {
        const val TABLE_NAME = "scores"
        const val COLUMN_NAME_COMPLETED_AT = "completed_at"
        const val COLUMN_NAME_SCORE = "score"
        const val COLUMN_NAME_ACCURACY = "accuracy"
    }

    private val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${ScoresEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${ScoresEntry.COLUMN_NAME_COMPLETED_AT} INTEGER," +
                "${ScoresEntry.COLUMN_NAME_SCORE} INTEGER," +
                "${ScoresEntry.COLUMN_NAME_ACCURACY} REAL)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${ScoresEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "piapp.db"
    }
}