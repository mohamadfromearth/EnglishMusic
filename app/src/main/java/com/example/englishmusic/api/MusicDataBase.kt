package com.example.englishmusic.api

import com.example.englishmusic.model.Search
import com.example.englishmusic.model.Song
import java.lang.Exception

class MusicDataBase {

    suspend fun getAllSong(album:String): Song? {

           return  RetrofitInstance.api.getSongs(album).body()

    }

    suspend fun getSearchSong(search:String): Search?{
       return RetrofitInstance.api.getSearch(search).body()
    }

    suspend fun getFavoriteSong(token:String):Song?{
        return RetrofitInstance.api.getFavorites(token).body()
    }


}