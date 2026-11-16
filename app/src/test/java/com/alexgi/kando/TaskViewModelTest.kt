package com.alexgi.kando

import com.alexgi.kando.model.Task
import com.alexgi.kando.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskViewModelTest {

    @Test
    fun testTaskFilteringLogic() {
        val tasks = listOf(
            Task(1, "Task 1", "Desc 1", TaskStatus.PENDING),
            Task(2, "Task 2", "Desc 2", TaskStatus.IN_PROGRESS),
            Task(3, "Task 3", "Desc 3", TaskStatus.DONE)
        )

        val pendingFiltered = tasks.filter { it.status == TaskStatus.PENDING }
        assertEquals(1, pendingFiltered.size)
        assertEquals("Task 1", pendingFiltered[0].title)

        val doneFiltered = tasks.filter { it.status == TaskStatus.DONE }
        assertEquals(1, doneFiltered.size)
        assertEquals("Task 3", doneFiltered[0].title)
    }
}
