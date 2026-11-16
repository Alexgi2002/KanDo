package com.alexgi.kando

import com.alexgi.kando.data.remote.TodoDto
import com.alexgi.kando.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoMappingTest {

    @Test
    fun testTodoCompletedMappingToStatus() {
        val todoCompleted = TodoDto(userId = 1, id = 1, title = "Test", completed = true)
        val status1 = if (todoCompleted.completed) TaskStatus.DONE else TaskStatus.PENDING
        assertEquals(TaskStatus.DONE, status1)

        val todoPending = TodoDto(userId = 1, id = 2, title = "Test 2", completed = false)
        val status2 = if (todoPending.completed) TaskStatus.DONE else TaskStatus.PENDING
        assertEquals(TaskStatus.PENDING, status2)
    }
}
