package com.example.englishmusic.viewmodel


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishmusic.model.*
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
    private val musicRepository: MusicRepository
) : ViewModel() {

 val Singer = MutableLiveData<Resource<Artist>>()

 val albums = MutableLiveData<Resource<Album>>()

 val search = MutableLiveData<Resource<Search>>()


 val login = MutableLiveData<Resource<Message>>()


 val isFavorite = MutableLiveData<Resource<IsFavorite>>()


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

    fun checkIsFavorite(token:String,favoriteId:FavoriteId) = viewModelScope.launch {
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