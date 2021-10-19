package com.example.englishmusic.model.song

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SerialRecentlySong(
    @SerializedName("song")
    val song:List<SongItem>
    ): Serializable
