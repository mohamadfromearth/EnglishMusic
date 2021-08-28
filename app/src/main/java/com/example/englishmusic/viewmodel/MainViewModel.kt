package com.example.englishmusic.viewmodel

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.englishmusic.exoPlayer.MusicServiceConnection
import com.example.englishmusic.exoPlayer.isPlayEnabled
import com.example.englishmusic.exoPlayer.isPlaying
import com.example.englishmusic.exoPlayer.isPrepared
import com.example.englishmusic.model.Constance.Companion.MEDIA_ROOT_ID
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.other.Event
import com.example.englishmusic.other.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URI
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection
): ViewModel() {

     val mediaItem = MutableLiveData<Resource<List<SongItem>>>()
   // val mediaItem: LiveData<Resource<List<SongItem>>> = _mediaItem



    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    val actionStatus = musicServiceConnection.actionStatus



    init {
        mediaItem.postValue(Resource.loading(null))

        musicServiceConnection.subscribe(MEDIA_ROOT_ID,object: MediaBrowserCompat.SubscriptionCallback(){
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                mediaItem.postValue(Resource.loading(null))
                super.onChildrenLoaded(MEDIA_ROOT_ID, children)
                Log.d("debug", "onChildrenLoaded: hahahahah ")

                val items = children.map {
                    SongItem(
                        0,
                        it.mediaId!!,
                        "",
                        it.description.subtitle.toString(),
                        it.description.iconUri.toString(),
                        it.description.extras?.getLong(METADATA_KEY_DURATION)?.toInt(),
                        it.description.title.toString(),
                        it.description.mediaUri.toString()
                    )

                }
                mediaItem.postValue(Resource.success(items,null))
            }
        })

    }

    fun addCustomAction(bundle:Bundle,action:String){
        musicServiceConnection.addCustomAction(bundle,action)
    }


    fun init(){


    }

fun playFromUrl(uri: Uri){
    musicServiceConnection.transportControls.playFromUri(uri,null)
}

    fun skipToNextSong(){
        musicServiceConnection.transportControls.skipToNext()
    }

  fun unsubscribe(album:String){
      musicServiceConnection.mediaBrowser.unsubscribe(album,object : MediaBrowserCompat.SubscriptionCallback(){})


  }


    fun skipToPreviousSong(){
       musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long){
        musicServiceConnection.transportControls.seekTo(pos)
    }

    fun playOrToggleSong(mediaItem: SongItem, toggle: Boolean = false){
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem._id ==
            curPlayingSong.value?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem._id, null)
        }
    }

override fun onCleared() {
    super.onCleared()
    musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object : MediaBrowserCompat.SubscriptionCallback(){})
}




}