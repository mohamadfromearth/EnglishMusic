package com.example.englishmusic.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SerializableSong(
    @SerializedName("song")
    val song:ArrayList<SongItem>

):Serializable
