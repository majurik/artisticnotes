package com.example.artisticnotes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.artisticnotes.data.MediaItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    viewModel: MediaViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val items by viewModel.items.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val typeFilter by viewModel.typeFilter.collectAsState()
    val categoryFilter by viewModel.categoryFilter.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var showFilterMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ArtisticNotes") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Фильтры")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Поиск по названию...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                },
                singleLine = true
            )

            // Активные фильтры
            if (typeFilter.isNotEmpty() || categoryFilter.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (typeFilter.isNotEmpty()) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setTypeFilter("") },
                            label = { Text(if (typeFilter == "AUDIO") "Аудио" else "Видео") },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                        )
                    }
                    if (categoryFilter.isNotEmpty()) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.setCategoryFilter("") },
                            label = { Text(categoryFilter) },
                            trailingIcon = { Icon(Icons.Default.Close, null, Modifier.size(16.dp)) }
                        )
                    }
                    TextButton(onClick = { viewModel.clearFilters() }) {
                        Text("Сбросить")
                    }
                }
            }

            // Список
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.LibraryMusic,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            if (searchQuery.isNotEmpty() || typeFilter.isNotEmpty() || categoryFilter.isNotEmpty())
                                "Ничего не найдено"
                            else
                                "Каталог пуст.\nНажмите + чтобы добавить материал",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        MediaCard(item = item, onClick = { onItemClick(item.id) })
                    }
                }
            }
        }
    }

    // Меню фильтров
    if (showFilterMenu) {
        AlertDialog(
            onDismissRequest = { showFilterMenu = false },
            title = { Text("Фильтры") },
            text = {
                Column {
                    Text("Тип:", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = typeFilter == "",
                            onClick = { viewModel.setTypeFilter("") },
                            label = { Text("Все") }
                        )
                        FilterChip(
                            selected = typeFilter == "AUDIO",
                            onClick = { viewModel.setTypeFilter("AUDIO") },
                            label = { Text("Аудио") }
                        )
                        FilterChip(
                            selected = typeFilter == "VIDEO",
                            onClick = { viewModel.setTypeFilter("VIDEO") },
                            label = { Text("Видео") }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Категория:", style = MaterialTheme.typography.labelLarge)
                    if (categories.isEmpty()) {
                        Text("Пока нет категорий", style = MaterialTheme.typography.bodySmall)
                    } else {
                        categories.forEach { cat ->
                            FilterChip(
                                selected = categoryFilter == cat,
                                onClick = {
                                    viewModel.setCategoryFilter(
                                        if (categoryFilter == cat) "" else cat
                                    )
                                },
                                label = { Text(cat) },
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFilterMenu = false }) {
                    Text("Готово")
                }
            }
        )
    }
}

@Composable
fun MediaCard(item: MediaItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.type == "AUDIO") Icons.Default.AudioFile else Icons.Default.VideoFile,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (item.type == "AUDIO")
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                if (item.type == "AUDIO") "Аудио" else "Видео",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                    if (item.category.isNotBlank()) {
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(item.category, style = MaterialTheme.typography.labelSmall)
                            }
                        )
                    }
                }
            }
        }
    }
}
