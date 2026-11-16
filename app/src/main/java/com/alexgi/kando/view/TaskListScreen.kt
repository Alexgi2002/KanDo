package com.alexgi.kando.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgi.kando.model.Task
import com.alexgi.kando.model.TaskStatus
import com.alexgi.kando.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = selectedTab, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        onTabSelected(pagerState.currentPage)
        val status = when (pagerState.currentPage) {
            0 -> TaskStatus.PENDING
            1 -> TaskStatus.IN_PROGRESS
            else -> TaskStatus.DONE
        }
        viewModel.setFilter(status)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KanDo") },
                actions = {
                    IconButton(onClick = { viewModel.loadTasks() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreate) {
                Icon(Icons.Default.Add, contentDescription = "Crear Tarea")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                }
            }

            // Tabs for Kanban states (Pendientes, En curso, Hechas)
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                    text = { Text("Pendientes") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    text = { Text("En curso") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    },
                    text = { Text("Hechas") }
                )
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    val pageStatus = when (page) {
                        0 -> TaskStatus.PENDING
                        1 -> TaskStatus.IN_PROGRESS
                        else -> TaskStatus.DONE
                    }
                    val tasksForPage = uiState.tasks.filter { it.status == pageStatus }

                    if (tasksForPage.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay tareas en este estado",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(tasksForPage, key = { it.id }) { task ->
                                TaskItemCard(
                                    task = task,
                                    onClick = { onNavigateToDetail(task.id) },
                                    onStatusChange = { newStatus ->
                                        viewModel.updateTaskStatus(task.id, newStatus)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: Task,
    onClick: () -> Unit,
    onStatusChange: (TaskStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box {
                StatusBadge(
                    status = task.status,
                    onClick = { expanded = true }
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Pendiente") },
                        onClick = {
                            expanded = false
                            onStatusChange(TaskStatus.PENDING)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("En curso") },
                        onClick = {
                            expanded = false
                            onStatusChange(TaskStatus.IN_PROGRESS)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hecha") },
                        onClick = {
                            expanded = false
                            onStatusChange(TaskStatus.DONE)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: TaskStatus,
    onClick: () -> Unit = {}
) {
    val (text, containerColor, contentColor) = when (status) {
        TaskStatus.PENDING -> Triple("Pendiente ▾", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
        TaskStatus.IN_PROGRESS -> Triple("En curso ▾", MaterialTheme.colorScheme.tertiaryContainer, MaterialTheme.colorScheme.onTertiaryContainer)
        TaskStatus.DONE -> Triple("Hecha ▾", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
    }

    Surface(
        color = containerColor,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
