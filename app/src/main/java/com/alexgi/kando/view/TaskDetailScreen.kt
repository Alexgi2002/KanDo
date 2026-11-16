package com.alexgi.kando.view

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgi.kando.model.TaskStatus
import com.alexgi.kando.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val task = uiState.tasks.find { it.id == taskId }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tarea") },
            text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteTask(taskId)
                        onNavigateBack()
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = task?.title ?: "Detalle de Tarea",
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        if (task == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Tarea no encontrada")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Sin descripción",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Estado actual", style = MaterialTheme.typography.titleMedium)
                StatusBadge(status = task.status)

                Spacer(modifier = Modifier.height(16.dp))

                Text("Cambiar estado", style = MaterialTheme.typography.titleMedium)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = task.status == TaskStatus.PENDING,
                        onClick = { viewModel.updateTaskStatus(task.id, TaskStatus.PENDING) },
                        label = { Text("Pendiente") }
                    )
                    FilterChip(
                        selected = task.status == TaskStatus.IN_PROGRESS,
                        onClick = { viewModel.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS) },
                        label = { Text("En curso") }
                    )
                    FilterChip(
                        selected = task.status == TaskStatus.DONE,
                        onClick = { viewModel.updateTaskStatus(task.id, TaskStatus.DONE) },
                        label = { Text("Hecha") }
                    )
                }
            }
        }
    }
}
