package com.example.englishmusic.model.userPlaylist

data class Song(
    val _id: String,
    val album: String,
    val artist: String,
    val cover: String,
    val duration: Int,
    val genre: String,
    val name: String,
    val songUrl: String
)