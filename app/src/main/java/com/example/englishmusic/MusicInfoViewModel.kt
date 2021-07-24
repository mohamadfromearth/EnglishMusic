package com.example.englishmusic


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishmusic.model.Album
import com.example.englishmusic.model.Artist
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class MusicInfoViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

 val Singer = MutableLiveData<Resource<Artist>>()

 val albums = MutableLiveData<Resource<Album>>()



    fun getArtist() = viewModelScope.launch{
        Singer.postValue(Resource(Status.LOADING,null,null))

        try{
            val response = musicRepository.getArtist()
            Singer.postValue(handleGetArtist(response))
        }catch (t:Throwable){

        }



    }


   fun getAlbum(artist:String) = viewModelScope.launch {
       albums.postValue(Resource(Status.LOADING,null,null))

         val response = musicRepository.getAlbum(artist)
         albums.postValue(handleGetAlbum(response))
   }


    private fun handleGetArtist(response:Response<Artist>):Resource<Artist>{
    if(response.isSuccessful){
        response.body()?.let { resultResponse ->
            return Resource.success(resultResponse)

        }
    }
     return Resource.error(response.message(),null)

    }

   private fun handleGetAlbum(response:Response<Album>):Resource<Album>{
       if (response.isSuccessful){
           response.body()?.let { resultResponse ->
               return Resource.success(resultResponse)

           }
       }
       return Resource.error(response.message(),null)
   }



}