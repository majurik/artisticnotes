package com.example.artisticnotes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    @Query("SELECT * FROM media_items ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE id = :id")
    suspend fun getById(id: Long): MediaItem?

    @Query("""
        SELECT * FROM media_items 
        WHERE (:query = '' OR title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
          AND (:type = '' OR type = :type)
          AND (:category = '' OR category = :category)
        ORDER BY addedAt DESC
    """)
    fun search(
        query: String,
        type: String,
        category: String
    ): Flow<List<MediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MediaItem): Long

    @Update
    suspend fun update(item: MediaItem)

    @Delete
    suspend fun delete(item: MediaItem)

    @Query("SELECT DISTINCT category FROM media_items WHERE category != '' ORDER BY category")
    fun observeCategories(): Flow<List<String>>
}
