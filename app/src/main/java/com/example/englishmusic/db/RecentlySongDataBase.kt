package com.example.englishmusic.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.englishmusic.model.SongItem
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint


@Database(
    entities = [SongItem::class],
    version = 1
)

abstract class RecentlySongDataBase:RoomDatabase() {

    abstract fun gerRecentlySongDao():RecentlySongDao





}