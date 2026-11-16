package com.alexgi.kando

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alexgi.kando.view.CreateTaskScreen
import com.alexgi.kando.view.TaskListScreen
import com.alexgi.kando.viewmodel.TaskViewModel
import com.alexgi.kando.view.theme.KanDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KanDoTheme {
                val viewModel: TaskViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        if (uiState.isCreatingTask) {
                            CreateTaskScreen(
                                viewModel = viewModel,
                                onNavigateBack = { viewModel.setCreatingTask(false) }
                            )
                        } else {
                            TaskListScreen(
                                viewModel = viewModel,
                                onNavigateToCreate = { viewModel.setCreatingTask(true) }
                            )
                        }
                    }
                }
            }
        }
    }
}
