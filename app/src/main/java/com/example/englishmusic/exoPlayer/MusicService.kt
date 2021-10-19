package com.example.englishmusic.exoPlayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import com.example.englishmusic.exoPlayer.callback.MusicPlaybackPreparer
import com.example.englishmusic.exoPlayer.callback.MusicPlayerEventListener
import com.example.englishmusic.exoPlayer.callback.MusicPlayerNotificationListener
import com.example.englishmusic.model.Constance.Companion.MEDIA_ROOT_ID
import com.example.englishmusic.model.Constance.Companion.NETWORK_ERROR
import com.example.englishmusic.model.Constance.Companion.NOTIFICATION_CHANNEL_ID
import com.example.englishmusic.model.SerializableDownloadSong
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.model.song.SerialRecentlySong
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import dagger.hilt.android.AndroidEntryPoint
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








    @RequiresApi(Build.VERSION_CODES.O)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "chanelName", NotificationManager.IMPORTANCE_NONE)

            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
        }




        musicNotificationManager = MusicNotificationManager(
            this,
            mediaSession.sessionToken,
            MusicPlayerNotificationListener(this),
            NOTIFICATION_CHANNEL_ID
        ){

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

        if (action == "searchFragment"){
            val serial = extras?.getSerializable("song")
            val song = serial as SerializableSong?
            firebaseMusicSource.fetchMediaDataFromSearch(song!!)
            notifyChildrenChanged(MEDIA_ROOT_ID)
        }
        if(action == "favorite"){
            val serial = extras?.getSerializable("song")
            val song = serial as SerializableSong?

                firebaseMusicSource.fetchFavoriteMetaData(song!!)

            notifyChildrenChanged(MEDIA_ROOT_ID)

        }
        if (action == "song"){
           val serial = extras?.getSerializable("song")
            val song = serial as SerializableSong?
           // Log.d("song", song!!.song[1].name)
            firebaseMusicSource.fetchMetaDataUi(song!!)

        }
       if(action == "songDownload"){
           val serial = extras?.getSerializable("song")
           val song = serial as SerializableDownloadSong
           firebaseMusicSource.fetchDownloadMedia(song)

       }

      if (action == "recentlySong"){
          val serial = extras?.getSerializable("song")
          val song = serial as SerialRecentlySong
          firebaseMusicSource.fetchRecentlySong(song)
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
       // exoPlayer.stop()
    }

    override fun onDestroy() {
        super.onDestroy()


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


