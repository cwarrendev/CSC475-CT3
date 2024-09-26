package com.example.todoapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ToDoRepository(context: Context) {
    private val dbHelper = ToDoDatabaseHelper(context)

    // Add a new task to the database
    fun addTask(task: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ToDoDatabaseHelper.COLUMN_TASK, task)
            put(ToDoDatabaseHelper.COLUMN_COMPLETED, 0)
        }
        db.insert(ToDoDatabaseHelper.TABLE_TODO, null, values)
    }

    // Delete a task from the database
    fun deleteTask(id: Long) {
        val db = dbHelper.writableDatabase
        db.delete(ToDoDatabaseHelper.TABLE_TODO, "${ToDoDatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    // Mark a task as completed
    fun markTaskCompleted(id: Long, completed: Boolean) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ToDoDatabaseHelper.COLUMN_COMPLETED, if (completed) 1 else 0)
        }
        db.update(ToDoDatabaseHelper.TABLE_TODO, values, "${ToDoDatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()))
    }

    // Get all tasks from the database
    fun getAllTasks(): List<ToDoItem> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ToDoDatabaseHelper.TABLE_TODO,
            null, null, null, null, null, null
        )
        val tasks = mutableListOf<ToDoItem>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_ID))
                val task = getString(getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_TASK))
                val completed = getInt(getColumnIndexOrThrow(ToDoDatabaseHelper.COLUMN_COMPLETED)) == 1
                tasks.add(ToDoItem(id, task, completed))
            }
        }
        cursor.close()
        return tasks
    }
}