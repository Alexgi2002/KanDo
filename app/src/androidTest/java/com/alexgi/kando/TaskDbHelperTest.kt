package com.alexgi.kando

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alexgi.kando.data.local.TaskDbHelper
import com.alexgi.kando.model.TaskStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDbHelperTest {

    @Test
    fun testDbHelperInsertionAndRetrieval() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val dbHelper = TaskDbHelper(context)

        val id = dbHelper.insertTask("Test Task DB", "Description test", TaskStatus.PENDING)
        assertNotNull(id)

        val tasks = dbHelper.getAllTasks()
        val found = tasks.find { it.id == id }
        assertNotNull(found)
        assertEquals("Test Task DB", found?.title)
        assertEquals(TaskStatus.PENDING, found?.status)

        dbHelper.deleteTask(id)
    }
}
