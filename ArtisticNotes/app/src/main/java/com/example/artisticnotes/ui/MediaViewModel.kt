package com.example.artisticnotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.artisticnotes.data.MediaItem
import com.example.artisticnotes.data.MediaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MediaViewModel(private val repository: MediaRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _typeFilter = MutableStateFlow("")      // "" | "AUDIO" | "VIDEO"
    val typeFilter: StateFlow<String> = _typeFilter.asStateFlow()

    private val _categoryFilter = MutableStateFlow("")
    val categoryFilter: StateFlow<String> = _categoryFilter.asStateFlow()

    val items: StateFlow<List<MediaItem>> = combine(
        _searchQuery,
        _typeFilter,
        _categoryFilter
    ) { query, type, category ->
        Triple(query, type, category)
    }.flatMapLatest { (query, type, category) ->
        repository.search(query, type, category)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<String>> = repository.observeCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: String) {
        _typeFilter.value = type
    }

    fun setCategoryFilter(category: String) {
        _categoryFilter.value = category
    }

    fun clearFilters() {
        _typeFilter.value = ""
        _categoryFilter.value = ""
        _searchQuery.value = ""
    }

    suspend fun getById(id: Long): MediaItem? = repository.getById(id)

    fun addItem(item: MediaItem, onDone: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.add(item)
            onDone(id)
        }
    }

    fun updateItem(item: MediaItem, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.update(item)
            onDone()
        }
    }

    fun deleteItem(item: MediaItem, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.delete(item)
            onDone()
        }
    }

    companion object {
        fun factory(repository: MediaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MediaViewModel(repository) as T
                }
            }
    }
}
