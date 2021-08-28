package com.example.englishmusic.exoPlayer

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.example.englishmusic.exoPlayer.callback.MusicPlaybackPreparer
import com.example.englishmusic.exoPlayer.callback.MusicPlayerEventListener
import com.example.englishmusic.exoPlayer.callback.MusicPlayerNotificationListener
import com.example.englishmusic.model.Constance.Companion.MEDIA_ROOT_ID
import com.example.englishmusic.model.Constance.Companion.NETWORK_ERROR
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.lang.IllegalStateException
import javax.inject.Inject

private const val SERVICE_TAG = "Music service"
private const val TAG = "debugbazi"


@AndroidEntryPoint
class MusicService:MediaBrowserServiceCompat() {



    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var firebaseMusicSource: ApiMusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var curPlayingSong: MediaMetadataCompat? = null

    private var album:String? = null

    private var isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
    }








    override fun onCreate() {
        super.onCreate()


        Log.d(TAG, "onCreate: ")









        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {

            it.putExtra("fromNotification","fromNotification")
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }


        sessionToken = mediaSession.sessionToken



        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this)
        ) {
            if(exoPlayer.duration != C.TIME_UNSET){
                curSongDuration = exoPlayer.duration
            }

        }

        val musicPlaybackPreparer = MusicPlaybackPreparer(firebaseMusicSource) {
            curPlayingSong = it
            preparePlayer(
                firebaseMusicSource.songs,
                it,
                true
            )
        }

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
        mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
        mediaSessionConnector.setPlayer(exoPlayer)

        musicPlayerEventListener = MusicPlayerEventListener(this)
        exoPlayer.addListener(musicPlayerEventListener)
        musicNotificationManager.showNotification(exoPlayer)


    }


    override fun onCustomAction(action: String, extras: Bundle?, result: Result<Bundle>) {
        super.onCustomAction(action, extras, result)
        if (action == "songFragment"){
            Log.d(TAG,extras?.getString("searchSong").toString() )
            serviceScope.launch {
                firebaseMusicSource.fetchMediaData(extras?.getString("album").toString())
            }
            notifyChildrenChanged(MEDIA_ROOT_ID)
           // notifyChildrenChanged(MEDIA_ROOT_ID)
        }
        if (action == "searchFragment"){
            Log.d(TAG,extras?.getString("searchSong").toString() )
            serviceScope.launch {
                firebaseMusicSource.fetchMediaDataFromSearch(extras?.getString("searchSong").toString())

            }
            notifyChildrenChanged(MEDIA_ROOT_ID)
        }

        if(action == "favorite"){

            serviceScope.launch {
                firebaseMusicSource.fetchFavoriteMetaData(extras?.getString("token").toString())
            }
            notifyChildrenChanged(MEDIA_ROOT_ID)

        }

    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return firebaseMusicSource.songs[windowIndex].description
        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val curSongIndex = if(curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare()
        exoPlayer.setMediaSource(firebaseMusicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()

        exoPlayer.removeListener(musicPlayerEventListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        Log.d(TAG, "onGetRoot: ")


        return BrowserRoot(MEDIA_ROOT_ID, null)
    }



    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

        Log.d(TAG, "on load childern"+parentId)


          when(parentId){
              parentId -> {
                  val resultsSent = firebaseMusicSource.whenReady { isInitialized ->
                      if (isInitialized) {
                         try {
                             result.sendResult(firebaseMusicSource.asMediaItems())
                         }catch (e:IllegalStateException){
                             //notifyChildrenChanged(parentId)
                         }

                          if (!isPlayerInitialized && firebaseMusicSource.songs.isNotEmpty()) {
                              preparePlayer(
                                  firebaseMusicSource.songs,
                                  firebaseMusicSource.songs[0],
                                  false
                              )
                              isPlayerInitialized = true
                          }
                      } else {
                          mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                          result.sendResult(null)
                      }
                  }

                  if(!resultsSent){
                      result.detach()
                  }
              }
          }





        }




    }


