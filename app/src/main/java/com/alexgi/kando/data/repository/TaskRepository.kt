package com.alexgi.kando.data.repository

import android.content.Context
import com.alexgi.kando.data.local.TaskDbHelper
import com.alexgi.kando.data.remote.TodoApiService
import com.alexgi.kando.model.Task
import com.alexgi.kando.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(
    context: Context,
    private val apiService: TodoApiService = TodoApiService.create()
) {
    private val dbHelper = TaskDbHelper(context)

    suspend fun getTasks(): List<Task> = withContext(Dispatchers.IO) {
        dbHelper.getAllTasks()
    }

    suspend fun fetchAndStoreInitialTodos(): Result<List<Task>> = withContext(Dispatchers.IO) {
        try {
            val localTasks = dbHelper.getAllTasks()
            if (localTasks.isEmpty()) {
                val todos = apiService.getTodos().take(15)
                val tasks = todos.map { todo ->
                    Task(
                        id = 0,
                        title = todo.title,
                        description = "Imported from JSONPlaceholder API",
                        status = if (todo.completed) TaskStatus.DONE else TaskStatus.PENDING
                    )
                }
                dbHelper.insertAll(tasks)
            }
            Result.success(dbHelper.getAllTasks())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addTask(title: String, description: String, status: TaskStatus): Task = withContext(Dispatchers.IO) {
        val id = dbHelper.insertTask(title, description, status)
        Task(id, title, description, status)
    }

    suspend fun updateTaskStatus(id: Long, status: TaskStatus) = withContext(Dispatchers.IO) {
        dbHelper.updateTaskStatus(id, status)
    }

    suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        dbHelper.deleteTask(id)
    }
}
