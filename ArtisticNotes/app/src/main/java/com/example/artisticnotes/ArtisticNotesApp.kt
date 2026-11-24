package com.example.artisticnotes

import android.app.Application
import com.example.artisticnotes.data.AppDatabase
import com.example.artisticnotes.data.MediaRepository

class ArtisticNotesApp : Application() {
    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { MediaRepository(database.mediaDao()) }
}
