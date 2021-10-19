package com.example.englishmusic.repository

import com.example.englishmusic.api.FollowedId
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.model.favorites.FavoriteId
import com.example.englishmusic.model.Username

class MusicRepository  {


    suspend fun getArtist() =
        RetrofitInstance.api.getArtist()



    suspend fun getAlbum(artist:String) =
        RetrofitInstance.api.getAlbum(artist)

    suspend fun getMyAlbum(token:String) =
        RetrofitInstance.api.getMyAlbums(token)

    suspend fun getSongs(album:String) =
        RetrofitInstance.api.getSongs(album)


    suspend fun getNewSongs() =
        RetrofitInstance.api.getNewSongs()

    suspend fun getTopSongs(artist:String) =
        RetrofitInstance.api.getTopSongs(artist)

    suspend fun getPlayLists() =
        RetrofitInstance.api.getPlaylists()


    suspend fun getSongByPlaylists(token:String,playList:String) =
        RetrofitInstance.api.getSongByPlaylist(token,playList)


    suspend fun getSearch(searchQuery:String) =
        RetrofitInstance.api.getSearch(searchQuery)

    suspend fun getFavorites(token: String) =
        RetrofitInstance.api.getFavorites(token)



    suspend fun loginUser(username:Username) =
        RetrofitInstance.api.loginUser(username)


    suspend fun getMyArtists(token:String) =
        RetrofitInstance.api.getMyArtists(token)


    suspend fun isFollowed(token:String,followedId: FollowedId) =
        RetrofitInstance.api.isFollowed(token,followedId)


    suspend fun isFavorite(token:String,favoriteId: FavoriteId)=
        RetrofitInstance.api.isFavorite(token,favoriteId)


    suspend fun isAlbumFavorite(token:String,favoriteId: FavoriteId) =
        RetrofitInstance.api.isAlbumFavorite(token,favoriteId)


    suspend fun getArtistsInfo(id:String) =
        RetrofitInstance.api.getArtistInfo(id)


    suspend fun getLyric(id:String) =
        RetrofitInstance.api.getLyric(id)




}