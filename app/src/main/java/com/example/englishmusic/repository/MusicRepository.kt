package com.example.englishmusic.repository

import com.example.englishmusic.api.RetrofitInstance

class MusicRepository {


    suspend fun getArtist() =
        RetrofitInstance.api.getArtist()



    suspend fun getAlbum(artist:String) =
        RetrofitInstance.api.getAlbum(artist)




}