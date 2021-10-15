package com.example.englishmusic.model

import android.graphics.Bitmap

data class DownloadSong(
    val id:String,
    val songName:String,
    val songPath:String,
    val songImg:Bitmap
)
