package com.alexgi.kando.model

enum class TaskStatus {
    PENDING, IN_PROGRESS, DONE
}

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val status: TaskStatus
)
