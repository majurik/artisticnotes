package com.example.artisticnotes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.artisticnotes.data.MediaItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: MediaViewModel,
    itemId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit
) {
    var item by remember { mutableStateOf<MediaItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemId) {
        item = viewModel.getById(itemId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Подробности") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(itemId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (item == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Запись не найдена")
            }
        } else {
            val current = item!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Иконка типа
                Icon(
                    imageVector = if (current.type == "AUDIO")
                        Icons.Default.AudioFile else Icons.Default.VideoFile,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = if (current.type == "AUDIO")
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.secondary
                )

                Text(current.title, style = MaterialTheme.typography.headlineSmall)

                DetailRow("Тип", if (current.type == "AUDIO") "Аудио" else "Видео")
                if (current.category.isNotBlank()) {
                    DetailRow("Категория", current.category)
                }
                if (current.description.isNotBlank()) {
                    DetailRow("Описание", current.description)
                }
                if (current.durationSeconds != null) {
                    val min = current.durationSeconds / 60
                    val sec = current.durationSeconds % 60
                    DetailRow("Длительность", "%d:%02d".format(min, sec))
                }
                if (current.sourceUri != null) {
                    DetailRow("Источник", current.sourceUri.take(80) + if (current.sourceUri.length > 80) "..." else "")
                }
                val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                    .format(Date(current.addedAt))
                DetailRow("Добавлено", date)
            }
        }
    }

    if (showDeleteDialog && item != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Удалить запись?") },
            text = {
                Text("Будет удалена только запись каталога. Исходный файл на устройстве не затрагивается.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteItem(item!!) {
                            showDeleteDialog = false
                            onBack()
                        }
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyLarge)
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}
