package com.example.englishmusic.exoPlayer

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Log
import androidx.core.net.toUri
import com.example.englishmusic.api.MusicDataBase
import com.example.englishmusic.model.SerializableDownloadSong
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.model.Song
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

class ApiMusicSource @Inject constructor(
  private val musicDatabase: MusicDataBase

) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData(album:String) = withContext(Dispatchers.Main) {
        state = State.STATE_INITIALIZING
        val allSongs = musicDatabase.getAllSong(album)

        if (allSongs != null) {
            songs = allSongs.map { song ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, song.artist)
                    .putString(METADATA_KEY_MEDIA_ID, song._id)
                    .putString(METADATA_KEY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song.cover)
                    .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                    .putString(METADATA_KEY_ALBUM_ART_URI, song.songUrl)
                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                    .putLong(METADATA_KEY_DURATION,song.duration!!.toLong())
                    .build()
            }



        }
        state = State.STATE_INITIALIZED
    }

    fun fetchMetaDataUi(song: SerializableSong){
        state = State.STATE_INITIALIZING
        val allSong = song
        songs = allSong.song.map { song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, song.artist)
                .putString(METADATA_KEY_MEDIA_ID, song._id)
                .putString(METADATA_KEY_TITLE, song.name)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.name)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.cover)
                .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.songUrl)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                .putLong(METADATA_KEY_DURATION,song.duration!!.toLong())

                .build()
        }
        state = State.STATE_INITIALIZED
    }

    fun fetchDownloadMedia(song:SerializableDownloadSong){
        state = State.STATE_INITIALIZING
        val allSong = song

        songs = allSong.song.map { song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, "downloading")
                .putString(METADATA_KEY_MEDIA_ID, song.id)
                .putString(METADATA_KEY_TITLE, song.songName)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.songName)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, "")
                .putString(METADATA_KEY_MEDIA_URI, song.songPath)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.songPath)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, "")
                .putLong(METADATA_KEY_DURATION,0)

                .putBitmap(METADATA_KEY_DISPLAY_ICON,song.songImg)
                .build()

        }
        state = State.STATE_INITIALIZED
    }


     fun fetchMediaDataFromSearch(song: SerializableSong){
        state = State.STATE_INITIALIZING
        val allSong = song
        if (allSong!=null){
            songs = allSong.song.map { song ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, song.artist)
                    .putString(METADATA_KEY_MEDIA_ID, song._id)
                    .putString(METADATA_KEY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song.cover)
                    .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                    .putString(METADATA_KEY_ALBUM_ART_URI, song.songUrl)
                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                    .putLong(METADATA_KEY_DURATION,song.duration!!.toLong())
                    .build()

            }
        }
        state = State.STATE_INITIALIZED
    }


     fun fetchFavoriteMetaData(song:SerializableSong){
        state = State.STATE_INITIALIZING
        val allSongs = song
        if (allSongs != null) {
            songs = allSongs.song.map { song ->
                MediaMetadataCompat.Builder()
                    .putString(METADATA_KEY_ARTIST, song.artist)
                    .putString(METADATA_KEY_MEDIA_ID, song._id)
                    .putString(METADATA_KEY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_TITLE, song.name)
                    .putString(METADATA_KEY_DISPLAY_ICON_URI, song.cover)
                    .putString(METADATA_KEY_MEDIA_URI, song.songUrl)
                    .putString(METADATA_KEY_ALBUM_ART_URI, song.songUrl)
                    .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.artist)
                    .putLong(METADATA_KEY_DURATION,song.duration!!.toLong())
                    .build()
            }



        }
        state = State.STATE_INITIALIZED

    }


    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
val bundle = Bundle().apply {
    putLong("duration",song.getLong(METADATA_KEY_DURATION))


}

        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .setExtras(bundle)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

    private val onReadyListeners = mutableListOf<(Boolean) -> Unit>()

    private var state: State = State.STATE_CREATED
        set(value) {
            if(value == State.STATE_INITIALIZED || value == State.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(state == State.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }

    fun whenReady(action: (Boolean) -> Unit): Boolean {
        return if(state == State.STATE_CREATED || state == State.STATE_INITIALIZING) {
            onReadyListeners += action
            false
        } else {
            action(state == State.STATE_INITIALIZED)
            true
        }
    }
}

enum class State {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR
}



