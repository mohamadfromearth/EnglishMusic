package com.example.englishmusic.model.downloads

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DownloadSongSerializable(
    @SerializedName("bitmap")
    val songBitmap:Bitmap
):Serializable
