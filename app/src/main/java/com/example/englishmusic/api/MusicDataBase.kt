package com.example.englishmusic.api

import com.example.englishmusic.model.Song
import java.lang.Exception

class MusicDataBase {

    suspend fun getAllSong(album:String): Song? {

           return  RetrofitInstance.api.getSongs(album).body()

    }
}