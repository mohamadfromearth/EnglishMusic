package com.example.englishmusic.exoPlayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.englishmusic.R
import com.example.englishmusic.model.Constance.Companion.NOTIFICATION_CHANNEL_ID
import com.example.englishmusic.model.Constance.Companion.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class MusicNotificationManager(
    private val context:Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
    private val newSongCallback:()->Unit
) {

    private val notificationManager:PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context,sessionToken)
        notificationManager = PlayerNotificationManager.
        Builder(context,
                NOTIFICATION_ID,
                NOTIFICATION_CHANNEL_ID,
                DescriptionAdapter(mediaController)
        ).setNotificationListener(notificationListener).build().apply {
            setSmallIcon(R.drawable.ic_baseline_music_note_24)
            setMediaSessionToken(sessionToken)
        }
    }

    fun showNotification(player:Player){
        notificationManager.setPlayer(player)
    }



    private inner class DescriptionAdapter(
        private val mediaController:MediaControllerCompat
    ):PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
          return  mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context).asBitmap()
                .load(mediaController.metadata.description.iconUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
            return null
        }

    }


}