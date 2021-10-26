package com.example.englishmusic.model.userPlaylist

data class UserPlayListItem(
    val _id: String,
    val name: String,
    val songs: List<Song>
)