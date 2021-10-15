package com.example.englishmusic.exoPlayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.englishmusic.R
import com.example.englishmusic.model.Constance.Companion.NOTIFICATION_CHANNEL_ID
import com.example.englishmusic.model.Constance.Companion.NOTIFICATION_ID
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.util.NotificationUtil.createNotificationChannel

class MusicNotificationManager(
    private val context:Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener,
     chanel:String,
    private val newSongCallback: () -> Unit

) {

    private val notificationManager:PlayerNotificationManager





    init {
        val mediaController = MediaControllerCompat(context,sessionToken)
        notificationManager = PlayerNotificationManager.
        Builder(context,
                NOTIFICATION_ID,
              chanel

        ).setMediaDescriptionAdapter(DescriptionAdapter(mediaController)).setNotificationListener(notificationListener).build().apply {
            setSmallIcon(R.drawable.ic_baseline_music_note_24)
            setMediaSessionToken(sessionToken)



        }



            //.setNotificationListener(notificationListener).build().apply {
           // setSmallIcon(R.drawable.ic_baseline_music_note_24)
           // setMediaSessionToken(sessionToken)

       // }
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
            newSongCallback()
            return  mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {

            if (mediaController.metadata.description.iconUri == Uri.parse("")){
                Glide.with(context).asBitmap()
                    .load(mediaController.metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON))
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            callback.onBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) = Unit
                    })
            }else{
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
                })}
            return null
        }

    }


}