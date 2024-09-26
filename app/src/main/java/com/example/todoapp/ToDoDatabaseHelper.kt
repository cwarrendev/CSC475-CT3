package com.example.todoapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ToDoDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TODO) // Create the tasks table
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TODO") // Drop the old table if it exists
        onCreate(db) // Create a new table
    }

    companion object {
        private const val DATABASE_NAME = "todo.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_TODO = "todo"
        const val COLUMN_ID = "_id"
        const val COLUMN_TASK = "task"
        const val COLUMN_COMPLETED = "completed"

        // SQL statement to create the tasks table
        private const val CREATE_TABLE_TODO = (
                "CREATE TABLE $TABLE_TODO (" +
                        "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$COLUMN_TASK TEXT NOT NULL, " +
                        "$COLUMN_COMPLETED INTEGER NOT NULL DEFAULT 0)"
                )
    }
}