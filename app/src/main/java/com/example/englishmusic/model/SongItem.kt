package com.example.englishmusic.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "recentlySong"
)
data class SongItem(
    val __v: Int,
    @PrimaryKey
    val _id: String,
    val album: String,
    val artist: String,
    val cover: String,
    val duration: Int?,
    val name: String,
    val songUrl: String,
    val genre:String
)