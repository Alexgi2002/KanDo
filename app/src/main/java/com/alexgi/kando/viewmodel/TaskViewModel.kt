package com.alexgi.kando.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alexgi.kando.data.local.TaskDbHelper
import com.alexgi.kando.data.remote.TodoApiService
import com.alexgi.kando.model.Task
import com.alexgi.kando.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val currentFilter: TaskStatus? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isCreatingTask: Boolean = false
)

class TaskViewModel(
    application: Application,
    private val dbHelper: TaskDbHelper,
    private val apiService: TodoApiService
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        TaskDbHelper(application),
        TodoApiService.create()
    )

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val syncResult = fetchAndStoreInitialTodos()
            if (syncResult.isFailure && _uiState.value.tasks.isEmpty()) {
                val local = withContext(Dispatchers.IO) { dbHelper.getAllTasks() }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = local,
                        filteredTasks = filterTasksList(local, it.currentFilter),
                        errorMessage = "Network error: Unable to load remote tasks. Showing offline local tasks."
                    )
                }
            } else {
                val tasks = withContext(Dispatchers.IO) { dbHelper.getAllTasks() }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        tasks = tasks,
                        filteredTasks = filterTasksList(tasks, it.currentFilter),
                        errorMessage = if (syncResult.isFailure) "Network warning: Could not refresh from API. Using local data." else null
                    )
                }
            }
        }
    }

    private suspend fun fetchAndStoreInitialTodos(): Result<List<Task>> = withContext(Dispatchers.IO) {
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

    fun setFilter(status: TaskStatus?) {
        _uiState.update { state ->
            state.copy(
                currentFilter = status,
                filteredTasks = filterTasksList(state.tasks, status)
            )
        }
    }

    fun updateTaskStatus(id: Long, newStatus: TaskStatus) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbHelper.updateTaskStatus(id, newStatus)
            }
            val tasks = withContext(Dispatchers.IO) { dbHelper.getAllTasks() }
            _uiState.update {
                it.copy(
                    tasks = tasks,
                    filteredTasks = filterTasksList(tasks, it.currentFilter)
                )
            }
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbHelper.deleteTask(id)
            }
            val tasks = withContext(Dispatchers.IO) { dbHelper.getAllTasks() }
            _uiState.update {
                it.copy(
                    tasks = tasks,
                    filteredTasks = filterTasksList(tasks, it.currentFilter)
                )
            }
        }
    }

    fun addTask(title: String, description: String, status: TaskStatus) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dbHelper.insertTask(title, description, status)
            }
            val tasks = withContext(Dispatchers.IO) { dbHelper.getAllTasks() }
            _uiState.update {
                it.copy(
                    tasks = tasks,
                    filteredTasks = filterTasksList(tasks, it.currentFilter),
                    isCreatingTask = false
                )
            }
        }
    }

    fun setCreatingTask(creating: Boolean) {
        _uiState.update { it.copy(isCreatingTask = creating) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun filterTasksList(tasks: List<Task>, filter: TaskStatus?): List<Task> {
        return if (filter == null) tasks else tasks.filter { it.status == filter }
    }
}
