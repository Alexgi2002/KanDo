package com.alexgi.kando.view

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.alexgi.kando.model.TaskStatus
import com.alexgi.kando.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    viewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf(TaskStatus.PENDING) }
    var titleError by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                title = { Text("Nueva Tarea") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = it.trim().length < 3
                },
                label = { Text("Título *") },
                isError = titleError,
                shape = RoundedCornerShape(16.dp),
                supportingText = {
                    if (titleError) {
                        Text("El título debe tener al menos 3 caracteres", color = MaterialTheme.colorScheme.error)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Text("Estado inicial", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedStatus == TaskStatus.PENDING,
                    onClick = { selectedStatus = TaskStatus.PENDING },
                    label = { Text("Pendiente") }
                )
                FilterChip(
                    selected = selectedStatus == TaskStatus.IN_PROGRESS,
                    onClick = { selectedStatus = TaskStatus.IN_PROGRESS },
                    label = { Text("En curso") }
                )
                FilterChip(
                    selected = selectedStatus == TaskStatus.DONE,
                    onClick = { selectedStatus = TaskStatus.DONE },
                    label = { Text("Hecha") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.trim().length >= 3) {
                        viewModel.addTask(title.trim(), description.trim(), selectedStatus)
                        onNavigateBack()
                    } else {
                        titleError = true
                    }
                },
                enabled = title.trim().length >= 3,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Tarea")
            }
        }
    }
}
