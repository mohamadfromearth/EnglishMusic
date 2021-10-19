package com.example.englishmusic.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.englishmusic.model.song.SongItem


@Database(
    entities = [SongItem::class],
    version = 1
)

abstract class RecentlySongDataBase:RoomDatabase() {

    abstract fun gerRecentlySongDao():RecentlySongDao





}