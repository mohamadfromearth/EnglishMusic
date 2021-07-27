package com.example.englishmusic.api

import com.example.englishmusic.model.Album
import com.example.englishmusic.model.Artist
import com.example.englishmusic.model.Song
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicApi {


    @GET("songs/getArtist")
    suspend fun getArtist():Response<Artist>


    @GET("songs/getAlbum/{artist}")
    suspend fun getAlbum(@Path("artist")artist:String):Response<Album>


    @GET("songs/getSongs/{album}")
    suspend fun getSongs(@Path("album")album:String):Response<Song>









}