package com.example.englishmusic.repository

import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.model.FavoriteId
import com.example.englishmusic.model.IsFavorite
import com.example.englishmusic.model.Username

class MusicRepository {


    suspend fun getArtist() =
        RetrofitInstance.api.getArtist()



    suspend fun getAlbum(artist:String) =
        RetrofitInstance.api.getAlbum(artist)


    suspend fun getSearch(searchQuery:String) =
        RetrofitInstance.api.getSearch(searchQuery)


    suspend fun loginUser(username:Username) =
        RetrofitInstance.api.loginUser(username)


    suspend fun isFavorite(token:String,favoriteId: FavoriteId)=
        RetrofitInstance.api.isFavorite(token,favoriteId)




}