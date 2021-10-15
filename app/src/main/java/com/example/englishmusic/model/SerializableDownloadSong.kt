package com.example.englishmusic.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SerializableDownloadSong(
    @SerializedName("song")
    val song:ArrayList<DownloadSong>
):Serializable
