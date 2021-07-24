package com.example.englishmusic.api

import com.example.englishmusic.model.Song
import java.lang.Exception

class MusicDataBase {

    suspend fun getAllSong(): Song? {

           return  RetrofitInstance.api.getSongs().body()

    }
}