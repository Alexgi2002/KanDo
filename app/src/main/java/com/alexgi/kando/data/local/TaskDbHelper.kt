package com.alexgi.kando.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.alexgi.kando.model.Task
import com.alexgi.kando.model.TaskStatus

class TaskDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "kando_tasks.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_STATUS = "status"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_STATUS TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = readableDatabase
        val cursor = db.query(TABLE_TASKS, null, null, null, null, null, "$COLUMN_ID DESC")
        cursor.use {
            val idCol = it.getColumnIndex(COLUMN_ID)
            val titleCol = it.getColumnIndex(COLUMN_TITLE)
            val descCol = it.getColumnIndex(COLUMN_DESCRIPTION)
            val statusCol = it.getColumnIndex(COLUMN_STATUS)

            while (it.moveToNext()) {
                val id = if (idCol != -1) it.getLong(idCol) else 0L
                val title = if (titleCol != -1) it.getString(titleCol) ?: "" else ""
                val description = if (descCol != -1) it.getString(descCol) ?: "" else ""
                val statusStr = if (statusCol != -1) it.getString(statusCol) ?: TaskStatus.PENDING.name else TaskStatus.PENDING.name
                val status = try {
                    TaskStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    TaskStatus.PENDING
                }
                tasks.add(Task(id, title, description, status))
            }
        }
        return tasks
    }

    fun insertTask(title: String, description: String, status: TaskStatus): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_STATUS, status.name)
        }
        return db.insert(TABLE_TASKS, null, values)
    }

    fun updateTaskStatus(id: Long, status: TaskStatus) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STATUS, status.name)
        }
        db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun insertAll(tasks: List<Task>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            for (task in tasks) {
                val values = ContentValues().apply {
                    put(COLUMN_TITLE, task.title)
                    put(COLUMN_DESCRIPTION, task.description)
                    put(COLUMN_STATUS, task.status.name)
                }
                db.insert(TABLE_TASKS, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}
