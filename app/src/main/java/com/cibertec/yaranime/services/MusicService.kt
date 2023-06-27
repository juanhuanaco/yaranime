package com.cibertec.yaranime.services

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.cibertec.yaranime.ApplicationClass
import com.cibertec.yaranime.MusicPlayerActivity
import com.cibertec.yaranime.R
import com.cibertec.yaranime.fragments.MusicDirectoryFragment
import com.cibertec.yaranime.receivers.NotificationReceiver
import com.google.android.exoplayer2.ExoPlayer
import com.squareup.picasso.Picasso

class MusicService : Service() {

    private var myBinder = MyBinder()
    private lateinit var mediaSession: MediaSessionCompat

    companion object {
        var exoPlayer : ExoPlayer? = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }



    inner class MyBinder : Binder() {
        fun currentService() : MusicService {
            return this@MusicService
        }
    }

    @SuppressLint("PrivateResource", "NotificationPermission")
    fun showNotification() {

        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(exoPlayer!!.currentMediaItem?.mediaMetadata?.title)
            .setContentText(exoPlayer!!.currentMediaItem?.mediaMetadata?.artist)
            .setSmallIcon(R.drawable.music_library_icon)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)

            .addAction(com.google.android.exoplayer2.ui.R.drawable.exo_notification_previous, "Previous", prevPendingIntent)
            .addAction(if (exoPlayer!!.isPlaying) com.google.android.exoplayer2.ui.R.drawable.exo_notification_pause else com.google.android.exoplayer2.ui.R.drawable.exo_notification_play, "Play", playPendingIntent)
            .addAction(com.google.android.exoplayer2.ui.R.drawable.exo_notification_next, "Next", nextPendingIntent)
            .addAction(R.drawable.close_icon, "Close", exitPendingIntent)

        if (exoPlayer!!.currentMediaItem?.mediaMetadata?.artworkUri != null){
            Glide.with(this).asBitmap().load(exoPlayer!!.currentMediaItem?.mediaMetadata?.artworkUri.toString()).into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notificationBuilder.setLargeIcon(resource)
                    startForeground(1, notificationBuilder.build())
                }
                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        }
        else {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.default_music))
            startForeground(1, notificationBuilder.build())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}