package com.paraverity.weighttrack

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * # COMP 4521 # LEE, Eun Shang     20245747     eslee@connect.ust.hk
 */

class WeightTrackDbHelper(context: Context) : SQLiteOpenHelper(context, WeightTrackDbHelper.DATABASE_NAME, null, WeightTrackDbHelper.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    object FeedReaderContract

    class SQLWeightEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "weightrecords"
            val COLUMN_DATE = "date"
            val COLUMN_WEIGHT = "weight"
            val COLUMN_POSITIVE = "positive"
            val COLUMN_NEGATIVE = "negative"
            val COLUMN_NOTES = "notes"
        }
    }

    companion object {



        val DATABASE_VERSION = 5
        val DATABASE_NAME = "WeightTrackDB.db"

        val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SQLWeightEntry.TABLE_NAME + " (" +
                        SQLWeightEntry.COLUMN_DATE + " INTEGER PRIMARY KEY," +
                        SQLWeightEntry.COLUMN_WEIGHT + " DECIMAL(5,2)," +
                        SQLWeightEntry.COLUMN_POSITIVE + " BOOLEAN," +
                        SQLWeightEntry.COLUMN_NEGATIVE + " BOOLEAN," +
                        SQLWeightEntry.COLUMN_NOTES + " TINYTEXT)"

        val SQL_SCHEMA = SQLWeightEntry.TABLE_NAME + " (" +
                SQLWeightEntry.COLUMN_DATE + " INTEGER PRIMARY KEY," +
                SQLWeightEntry.COLUMN_WEIGHT + " DECIMAL(5,2)," +
                SQLWeightEntry.COLUMN_POSITIVE + " BOOLEAN," +
                SQLWeightEntry.COLUMN_NEGATIVE + " BOOLEAN," +
                SQLWeightEntry.COLUMN_NOTES + " TINYTEXT)"

        private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + SQLWeightEntry.TABLE_NAME

        val headers = arrayOf(SQLWeightEntry.COLUMN_DATE, SQLWeightEntry.COLUMN_WEIGHT, SQLWeightEntry.COLUMN_POSITIVE, SQLWeightEntry.COLUMN_NEGATIVE, SQLWeightEntry.COLUMN_NOTES)
    }
}
