package com.example.englishmusic

import com.example.englishmusic.model.downloads.DownloadSong

interface IDeleteDownload {
    fun deleteDownload(downloadSong: DownloadSong)
}