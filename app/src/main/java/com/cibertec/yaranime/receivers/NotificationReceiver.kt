package com.cibertec.yaranime.receivers

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import com.cibertec.yaranime.ApplicationClass
import com.cibertec.yaranime.MusicPlayerActivity
import com.cibertec.yaranime.services.MusicService
import kotlin.system.exitProcess

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> MusicService.exoPlayer!!.seekToPreviousMediaItem()
            ApplicationClass.PLAY -> if(MusicService.exoPlayer!!.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> MusicService.exoPlayer!!.seekToNextMediaItem()
            ApplicationClass.EXIT -> {
                MusicPlayerActivity.musicService!!.stopForeground(Service.STOP_FOREGROUND_REMOVE)
                MusicPlayerActivity.musicService = null
                exitProcess(0)
            }
        }
    }
    private fun playMusic(){
        MusicService.exoPlayer!!.play()
        MusicPlayerActivity.musicService!!.showNotification()
    }
    private fun pauseMusic(){
        MusicService.exoPlayer!!.pause()
        MusicPlayerActivity.musicService!!.showNotification()
    }
}