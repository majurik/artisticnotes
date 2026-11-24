package com.example.artisticnotes.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val type: String,                    // "AUDIO" или "VIDEO"
    val category: String = "",
    val description: String = "",
    val sourceUri: String? = null,
    val durationSeconds: Long? = null,
    val addedAt: Long = System.currentTimeMillis()
)
