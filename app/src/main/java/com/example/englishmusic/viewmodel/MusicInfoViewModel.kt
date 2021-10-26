package com.example.englishmusic.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishmusic.api.FollowedId
import com.example.englishmusic.db.RecentlySongDao
import com.example.englishmusic.model.*
import com.example.englishmusic.model.albums.Album
import com.example.englishmusic.model.artist.Artist
import com.example.englishmusic.model.artist.ArtistInfo
import com.example.englishmusic.model.downloads.DownloadSong
import com.example.englishmusic.model.favorites.FavoriteId
import com.example.englishmusic.model.favorites.IsFavorite
import com.example.englishmusic.model.lyric.Lyric
import com.example.englishmusic.model.song.Song
import com.example.englishmusic.model.song.SongItem
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MusicInfoViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val recentlySongDao: RecentlySongDao
) : ViewModel() {

 val Singer = MutableLiveData<Resource<Artist>>()

    val myArtists = MutableLiveData<Resource<Artist>>()

 val albums = MutableLiveData<Resource<Album>>()

 val search = MutableLiveData<Resource<Search>>()


  val topSongs = MutableLiveData<Resource<Song>>()

    val playlistsSong = MutableLiveData<Resource<Song>>()


    val playlist = MutableLiveData<Resource<Playlist>>()

    val myAlbums = MutableLiveData<Resource<Album>>()

    val isAlbumFavorite = MutableLiveData<Resource<IsFavorite>>()

    val shouldUpdateMyArtist = MutableLiveData<Boolean>()


 val login = MutableLiveData<Resource<Message>>()


 val isFavorite = MutableLiveData<Resource<IsFavorite>>()

    val isFollowed = MutableLiveData<Resource<IsFavorite>>()

   val songs = MutableLiveData<Resource<Song>>()

   val downloadedSongs = MutableLiveData<List<DownloadSong>>()

    val newSongs = MutableLiveData<Resource<Song>>()


    val favorites = MutableLiveData<Resource<Song>>()


    val artistInfo = MutableLiveData<Resource<ArtistInfo>>()


    val lyric = MutableLiveData<Resource<Lyric>>()



    var subtitle:String=""





    fun shouldUpdateMyArtist(value:Boolean){
        shouldUpdateMyArtist.value = value
    }


    fun getArtist() = viewModelScope.launch{
        Singer.postValue(Resource(Status.LOADING,null,""))

        try{
            val response = musicRepository.getArtist()
            Singer.postValue(handleGetArtist(response))
        }catch (t:Throwable){
          when(t){
              is IOException -> Singer.postValue(Resource.error("Network failure",null))
          }
        }



    }
    fun getMyArtists(token:String) = viewModelScope.launch {
        myArtists.postValue(Resource(Status.LOADING,null,""))

        try {
            val response = musicRepository.getMyArtists(token)
          myArtists.postValue(handleResponse(response))

        }catch (t:Throwable){
            when(t){
                is IOException -> myArtists.postValue(Resource.error("Network failure",null))
            }
        }
    }
    fun checkIsFollowed(token:String,followedId: FollowedId) = viewModelScope.launch {
        isFollowed.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.isFollowed(token,followedId)
            isFollowed.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> isFollowed.postValue(Resource(Status.ERROR,null,""))
            }

        }
    }
    fun getAlbum(artist:String) = viewModelScope.launch {
       albums.postValue(Resource(Status.LOADING,null,""))
       try{
           val response = musicRepository.getAlbum(artist)
           albums.postValue(handleGetAlbum(response))
       }catch (t:Throwable){
           when(t){
               is IOException -> albums.postValue(Resource.error("Network failure",null))
           }
       }


   }
    fun getMyAlbum(token:String) = viewModelScope.launch {
       myAlbums.postValue(Resource.loading(null))
        try {
            val response = musicRepository.getMyAlbum(token)
            myAlbums.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> myAlbums.postValue(Resource.error("Netwoek failure",null))
            }
        }
    }
    fun checkIsAlbumFavorite(token: String,favoriteId: FavoriteId) = viewModelScope.launch {
        isAlbumFavorite.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.isAlbumFavorite(token,favoriteId)
            isAlbumFavorite.postValue(handleResponse(response))
        }catch (t:Throwable){

        }
    }
    fun getSong(album:String) = viewModelScope.launch {
       songs.postValue(Resource(Status.LOADING,null,""))
       try {
           val response = musicRepository.getSongs(album)
           songs.postValue(handleResponse(response))
       }catch (t:Throwable){
           when(t){
             is IOException -> songs.postValue(Resource.error("Network failure",null))
           }
       }

   }
    fun getTopSongs(artist:String) = viewModelScope.launch {
        topSongs.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.getTopSongs(artist)
            topSongs.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> topSongs.postValue(Resource(Status.ERROR,null,""))
            }
        }
    }
    fun getPlaylistSong(token:String,playlist:String) = viewModelScope.launch {
        playlistsSong.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.getSongByPlaylists(token,playlist)
            playlistsSong.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> playlistsSong.postValue(Resource(Status.ERROR,null,"Network failure"))
            }
        }

    }
    fun getNewSong() = viewModelScope.launch {
        newSongs.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.getNewSongs()
            newSongs.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> newSongs.postValue(Resource(Status.ERROR,null,""))
            }

        }
    }
    fun getPlayLists() = viewModelScope.launch {
        playlist.postValue(Resource.loading(null))
        try {
            val response = musicRepository.getPlayLists()
            playlist.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> playlist.postValue(Resource.error("",null))
            }
        }
    }
    fun getFavorites(token:String) = viewModelScope.launch {
        favorites.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.getFavorites(token)
            favorites.postValue(handleResponse(response))
        }catch (t:Throwable){
             when(t){
                 is IOException -> favorites.postValue(Resource.error("Network failure",null))
             }
        }
    }
    fun getSearch(searchQuery:String) = viewModelScope.launch {
      search.postValue(Resource(Status.LOADING,null,""))
      try{
          val response = musicRepository.getSearch(searchQuery)
          search.postValue(handleGetSearch(response))
      }catch (t:Throwable){
          when(t){
              is IOException -> search.postValue(Resource.error("Network failure",null))
          }
      }


  }
    fun loginUser(username:Username) = viewModelScope.launch {
        login.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.loginUser(username)
            login.postValue(handleLoginUser(response, response.headers()["x-auth-token"].toString()))

            
        }catch (t:Throwable){
            when(t){
                is IOException -> login.postValue(Resource.error("Network failure",null))
            }
        }
    }
    fun checkIsFavorite(token:String,favoriteId: FavoriteId) = viewModelScope.launch {
        isFavorite.postValue(Resource(Status.LOADING,null,""))
        try{
            val response = musicRepository.isFavorite(token,favoriteId)
            isFavorite.postValue(handleIsFavorite(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> isFavorite.postValue(Resource.error("Network failure",null))
            }
        }
    }


    fun getLyric(id:String) = viewModelScope.launch {
        lyric.postValue(Resource(Status.LOADING,null,""))
        try {
            val response = musicRepository.getLyric(id)
            lyric.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> lyric.postValue(Resource.error("Network failure",null))
            }
        }
    }




    fun getArtistInfo(id:String) = viewModelScope.launch {
        artistInfo.postValue(Resource(Status.LOADING,null,""))
        try{
            val response =  musicRepository.getArtistsInfo(id)
            artistInfo.postValue(handleResponse(response))
        }catch (t:Throwable){
            when(t){
                is IOException -> artistInfo.postValue(Resource(Status.ERROR,null,"Network failure"))
            }
        }
    }

   fun getRecentlySong() =
       recentlySongDao.getAllRecentlySongs(10)


   fun upsertRecentlyPlayedSong(song: SongItem) = viewModelScope.launch {
       recentlySongDao.upsert(song)
   }



    private fun handleGetArtist(response:Response<Artist>):Resource<Artist>{
    if(response.isSuccessful){
        response.body()?.let { resultResponse ->
            return Resource.success(resultResponse,null)

        }
    }
     return Resource.error(response.message(),null)

    }
    private fun handleGetAlbum(response:Response<Album>):Resource<Album>{
       if (response.isSuccessful){
           response.body()?.let { resultResponse ->
               return Resource.success(resultResponse,null)

           }
       }
       return Resource.error(response.message(),null)
   }
    private fun<T> handleResponse(response:Response<T>):Resource<T>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
                return Resource.success(resultResponse,null)

            }
        }
        return Resource.error(response.message(),null)
    }
    private fun handleGetSearch(response:Response<Search>):Resource<Search>{
        if (response.isSuccessful){
            response.body()?.let {  resultResponse ->
                return Resource.success(resultResponse,null)
            }
        }
        return Resource.error(response.message(),null)
    }
    private fun handleLoginUser(response:Response<Message>,message:String):Resource<Message>{
        if (response.isSuccessful){
            response.body()?.let { resultResponse ->
            return Resource.success(resultResponse,message)

            }
        }
        return Resource.error(response.message(),null)
    }
    private fun handleIsFavorite(response:Response<IsFavorite>):Resource<IsFavorite>{
     if (response.isSuccessful){
         response.body()?.let { resultResponse ->
             return Resource.success(resultResponse,"")

         }
     }
        return Resource.error(response.message(),null)
    }



}