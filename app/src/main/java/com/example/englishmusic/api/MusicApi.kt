package com.example.englishmusic.api

import com.example.englishmusic.model.*
import retrofit2.Response
import retrofit2.http.*

interface MusicApi {


    @GET("songs/getArtist")
    suspend fun getArtist():Response<Artist>


    @GET("songs/getAlbum/{artist}")
    suspend fun getAlbum(@Path("artist")artist:String):Response<Album>


    @GET("songs/getSongs/{album}")
    suspend fun getSongs(@Path("album")album:String):Response<Song>

    @GET("songs/getSearch/{search}")
    suspend fun getSearch(@Path("search")search:String):Response<Search>


    @POST("songs/registerUser")
    suspend fun registerUser(@Body username: Username):Response<Message>


    @POST("songs/loginUser")
    suspend fun loginUser(@Body username: Username):Response<Message>





    @POST("songs/addFavorites")
    suspend fun addFavorites(@Header("x-auth-token")token:String, @Body song:SongItemFav):Response<Message>


    @POST("songs/deleteFavorites")
    suspend fun deleteFavorite(@Header("x-auth-token")token:String,@Body deleteFavoriteBody: DeleteFavoriteBody):Response<Message>


    @GET("songs/getFavorites")
    suspend fun getFavorites(@Header("x-auth-token")token: String):Response<Song>


    @POST("songs/isFavorite")
    suspend fun isFavorite(@Header("x-auth-token")token:String,@Body favoriteId:FavoriteId):Response<IsFavorite>





}