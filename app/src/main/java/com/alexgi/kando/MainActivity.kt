package com.alexgi.kando

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.alexgi.kando.view.CreateTaskScreen
import com.alexgi.kando.view.TaskDetailScreen
import com.alexgi.kando.view.TaskListScreen
import com.alexgi.kando.view.theme.KanDoTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed interface Screen {
    object List : Screen
    object Create : Screen
    data class Detail(val taskId: Long) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanDoTheme {
                val viewModel: com.alexgi.kando.viewmodel.TaskViewModel by viewModel()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.List) }
                var selectedTab by remember { mutableIntStateOf(0) }

                BackHandler(enabled = currentScreen !is Screen.List) {
                    currentScreen = Screen.List
                }

                Scaffold(
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (val screen = currentScreen) {
                            is Screen.List -> {
                                TaskListScreen(
                                    viewModel = viewModel,
                                    selectedTab = selectedTab,
                                    onTabSelected = { selectedTab = it },
                                    onNavigateToCreate = { currentScreen = Screen.Create },
                                    onNavigateToDetail = { taskId -> currentScreen = Screen.Detail(taskId) }
                                )
                            }
                            is Screen.Create -> {
                                CreateTaskScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { currentScreen = Screen.List }
                                )
                            }
                            is Screen.Detail -> {
                                TaskDetailScreen(
                                    taskId = screen.taskId,
                                    viewModel = viewModel,
                                    onNavigateBack = { currentScreen = Screen.List }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
