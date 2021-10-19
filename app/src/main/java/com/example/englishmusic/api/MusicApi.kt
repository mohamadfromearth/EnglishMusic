package com.example.englishmusic.api

import com.example.englishmusic.model.*
import com.example.englishmusic.model.albums.AddAlbum
import com.example.englishmusic.model.albums.Album
import com.example.englishmusic.model.artist.AddArtist
import com.example.englishmusic.model.artist.Artist
import com.example.englishmusic.model.artist.ArtistId
import com.example.englishmusic.model.artist.ArtistInfo
import com.example.englishmusic.model.favorites.FavoriteId
import com.example.englishmusic.model.favorites.IsFavorite
import com.example.englishmusic.model.lyric.Lyric
import com.example.englishmusic.model.song.Song
import retrofit2.Response
import retrofit2.http.*

interface MusicApi {


    @GET("songs/getArtist")
    suspend fun getArtist():Response<Artist>


    @GET("songs/getAlbum/{artist}")
    suspend fun getAlbum(@Path("artist")artist:String):Response<Album>


    @GET("songs/getSongs/{album}")
    suspend fun getSongs(@Path("album")album:String):Response<Song>


    @GET("songs/getAllSong")
    suspend fun getNewSongs():Response<Song>


    @GET("songs/getTopSongs/{artist}")
    suspend fun getTopSongs(@Path("artist")artist:String):Response<Song>


    @PUT("songs/increaseViewAndPlay")
    suspend fun increaseViewAndPlay(@Body id:Id):Response<Boolean>

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


    @GET("songs/getMyAlbums")
    suspend fun getMyAlbums(@Header("x-auth-token")token:String):Response<Album>

    @POST("songs/addAlbum")
    suspend fun addAlbum(@Header("x-auth-token")token:String,@Body album: AddAlbum):Response<Message>

    @POST("songs/deleteAlbum")
    suspend fun deleteAlbum(@Header("x-auth-token")token: String,@Body albumId:AlbumId):Response<Message>


    @POST("songs/isAlbumFavorite")
    suspend fun isAlbumFavorite(@Header("x-auth-token")token:String,@Body favoriteId: FavoriteId):Response<IsFavorite>


    @GET("songs/getPlaylists")
    suspend fun getPlaylists():Response<Playlist>


    @POST("songs/isFavorite")
    suspend fun isFavorite(@Header("x-auth-token")token:String,@Body favoriteId: FavoriteId):Response<IsFavorite>


    @POST("songs/selectArtist")
    suspend fun selectArtist(@Header("x-auth-token")token:String,@Body artist: AddArtist):Response<Message>

    @POST("songs/unselectArtist")
    suspend fun unSelectArtist(@Header("x-auth-token")token:String,@Body artistId: ArtistId):Response<Message>


    @POST("songs/isFollowed")
    suspend fun isFollowed(@Header("x-auth-token")token:String,@Body followedId:FollowedId):Response<IsFavorite>

    @GET("songs/getSelectedArtists")
    suspend fun getMyArtists(@Header("x-auth-token")token:String):Response<Artist>


    @GET("songs/getSongByPlayLists/{playList}")
    suspend fun getSongByPlaylist(
        @Header("x-auth-token")token:String,
        @Path("playList")playlist:String):Response<Song>



    @GET("songs/getArtistInfo/{id}")
    suspend fun getArtistInfo(@Path("id")id:String):Response<ArtistInfo>


    @GET("songs/getLyric/{id}")
    suspend fun getLyric(@Path("id")id:String):Response<Lyric>


}

data class FollowedId(
    val followId:String
)

data class AlbumId(
    val albumId:String
)