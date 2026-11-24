package com.example.artisticnotes.data

import kotlinx.coroutines.flow.Flow

class MediaRepository(private val dao: MediaDao) {

    fun observeAll(): Flow<List<MediaItem>> = dao.observeAll()

    fun search(query: String, type: String, category: String): Flow<List<MediaItem>> =
        dao.search(query, type, category)

    fun observeCategories(): Flow<List<String>> = dao.observeCategories()

    suspend fun getById(id: Long): MediaItem? = dao.getById(id)

    suspend fun add(item: MediaItem): Long = dao.insert(item)

    suspend fun update(item: MediaItem) = dao.update(item)

    suspend fun delete(item: MediaItem) = dao.delete(item)
}
