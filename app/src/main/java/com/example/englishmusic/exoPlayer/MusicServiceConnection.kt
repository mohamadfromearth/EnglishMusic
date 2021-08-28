package com.example.englishmusic.exoPlayer

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.englishmusic.model.Constance.Companion.NETWORK_ERROR
import com.example.englishmusic.other.Event
import com.example.englishmusic.other.Resource
import kotlin.math.log

class MusicServiceConnection(
    context:Context

    ) {
    private val _isConnected = MutableLiveData<Event<Resource<Boolean>>>()
    val isConnected: LiveData<Event<Resource<Boolean>>> = _isConnected

    private val _networkError = MutableLiveData<Event<Resource<Boolean>>>()
    val networkError: LiveData<Event<Resource<Boolean>>> = _networkError

    private val _playbackState = MutableLiveData<PlaybackStateCompat?>()
    val playbackState: LiveData<PlaybackStateCompat?> = _playbackState

     val curPlayingSong = MutableLiveData<MediaMetadataCompat?>()


     val actionStatus = MutableLiveData<String>()





    lateinit var mediaController: MediaControllerCompat

    private val mediaBrowserConnectionCallback = MediaBrowserConnectionCallback(context)




     val mediaBrowser :MediaBrowserCompat  = MediaBrowserCompat(
    context,
    ComponentName(
    context,
    MusicService::class.java
    ),
    mediaBrowserConnectionCallback,
    null
    ).apply{
        connect()
    }





    val transportControls: MediaControllerCompat.TransportControls
        get() = mediaController.transportControls



    fun addCustomAction(bundle:Bundle,action: String){
        mediaBrowser.sendCustomAction(action,bundle,null)
    }


    fun subscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {


        mediaBrowser.subscribe(parentId, callback)
    }

    fun unsubscribe(parentId: String, callback: MediaBrowserCompat.SubscriptionCallback) {
        mediaBrowser.unsubscribe(parentId, callback)
    }



    private inner class MediaBrowserConnectionCallback(
        private val context: Context
    ) : MediaBrowserCompat.ConnectionCallback() {




        override fun onConnected() {
            Log.d("MusicServiceConnection", "CONNECTED")
            mediaController = MediaControllerCompat(context, mediaBrowser.sessionToken).apply {
                registerCallback(MediaContollerCallback())
            }
            _isConnected.postValue(Event(Resource.success(true,null)))
        }

        override fun onConnectionSuspended() {
            Log.d("MusicServiceConnection", "SUSPENDED")

            _isConnected.postValue(Event(Resource.error(
                "The connection was suspended", false
            )))
        }

        override fun onConnectionFailed() {
            Log.d("MusicServiceConnection", "FAILED")

            _isConnected.postValue(Event(Resource.error(
                "Couldn't connect to media browser", false
            )))
        }
    }

    private inner class MediaContollerCallback : MediaControllerCompat.Callback() {



        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            _playbackState.postValue(state)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

            curPlayingSong.postValue(metadata)
        }

        override fun onSessionEvent(event: String?, extras: Bundle?) {
            super.onSessionEvent(event, extras)
            when(event) {
                NETWORK_ERROR -> _networkError.postValue(
                    Event(
                        Resource.error(
                            "Couldn't connect to the server. Please check your internet connection.",
                            null
                        )
                    )
                )
            }
        }

        override fun onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended()
        }
    }










}