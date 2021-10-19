package com.example.englishmusic.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.englishmusic.model.downloads.DownloadSong

import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val context: Context
):ViewModel() {

    val downloadedSong  = MutableLiveData<MutableList<DownloadSong>>()

    val songImage = MutableLiveData<Bitmap>()


    fun getDownloadSong(){
        val song = ArrayList<DownloadSong>()
        val file = context.getExternalFilesDir(Environment.getDataDirectory().absolutePath)
            ?.listFiles()
        val mmr = MediaMetadataRetriever()
        var inputStream: InputStream? = null
        file?.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }?.map {
            mmr.setDataSource(it.absolutePath)

            inputStream = ByteArrayInputStream(mmr.embeddedPicture)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            song.add(
                DownloadSong(it.hashCode().toString(),
                it.name,
                it.absolutePath,
                bitmap)
            )
        }
        downloadedSong.postValue(song)
    }

}