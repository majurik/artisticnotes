package com.example.artisticnotes.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.artisticnotes.data.MediaItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    viewModel: MediaViewModel,
    itemId: Long?,          // null = создание
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("AUDIO") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var sourceUri by remember { mutableStateOf<String?>(null) }
    var durationSeconds by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(itemId != null) }

    // Загрузка существующей записи
    LaunchedEffect(itemId) {
        if (itemId != null) {
            val item = viewModel.getById(itemId)
            if (item != null) {
                title = item.title
                type = item.type
                category = item.category
                description = item.description
                sourceUri = item.sourceUri
                durationSeconds = item.durationSeconds?.toString() ?: ""
            }
            isLoading = false
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        sourceUri = uri?.toString()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId == null) "Добавить" else "Редактировать") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Название (обязательное)
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("Название *") },
                    isError = titleError != null,
                    supportingText = titleError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Тип
                Text("Тип материала", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "AUDIO",
                        onClick = { type = "AUDIO" },
                        label = { Text("Аудио") }
                    )
                    FilterChip(
                        selected = type == "VIDEO",
                        onClick = { type = "VIDEO" },
                        label = { Text("Видео") }
                    )
                }

                // Категория
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Категория") },
                    placeholder = { Text("музыка, обучение, лекции...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Описание
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                // Продолжительность
                OutlinedTextField(
                    value = durationSeconds,
                    onValueChange = { durationSeconds = it.filter { c -> c.isDigit() } },
                    label = { Text("Длительность (сек)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Выбор файла
                OutlinedButton(
                    onClick = {
                        val mimeTypes = if (type == "AUDIO") {
                            arrayOf("audio/*")
                        } else {
                            arrayOf("video/*")
                        }
                        filePicker.launch(mimeTypes)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (sourceUri != null) "Файл выбран"
                        else "Выбрать файл"
                    )
                }

                if (sourceUri != null) {
                    Text(
                        text = "URI: ${sourceUri!!.take(60)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Кнопка сохранения
                Button(
                    onClick = {
                        val trimmed = title.trim()
                        if (trimmed.isEmpty()) {
                            titleError = "Введите название"
                            return@Button
                        }
                        val item = MediaItem(
                            id = itemId ?: 0,
                            title = trimmed,
                            type = type,
                            category = category.trim(),
                            description = description.trim(),
                            sourceUri = sourceUri,
                            durationSeconds = durationSeconds.toLongOrNull()
                        )
                        if (itemId == null) {
                            viewModel.addItem(item) { onBack() }
                        } else {
                            viewModel.updateItem(item) { onBack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}
