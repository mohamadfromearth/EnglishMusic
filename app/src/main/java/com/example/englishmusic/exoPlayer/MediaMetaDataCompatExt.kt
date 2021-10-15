package com.example.englishmusic.exoPlayer

import android.support.v4.media.MediaMetadataCompat
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.model.SongItemFav

fun MediaMetadataCompat.toSong():SongItem? {
    return description?.let {

            SongItem(
                0,
                it.mediaId.toString(),
                "",
                it.subtitle.toString(),
                it.iconUri.toString(),
                it.describeContents(),
                it.title.toString(),
                it.mediaUri.toString(),
                ""

            )

    }
}

fun MediaMetadataCompat.toSongFav():SongItemFav? {
    return description?.let {

        SongItemFav(
            0,
            it.mediaId.toString(),
            "",
            it.subtitle.toString(),
            it.iconUri.toString(),
            it.describeContents(),
            it.title.toString(),
            it.mediaUri.toString()

        )

    }
}