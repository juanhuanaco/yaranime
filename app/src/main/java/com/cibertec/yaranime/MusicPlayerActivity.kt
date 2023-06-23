package com.cibertec.yaranime

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.cibertec.yaranime.fragments.MusicDirectoryFragment
import com.cibertec.yaranime.services.MusicService
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MusicPlayerActivity : AppCompatActivity(), ServiceConnection {

    lateinit var playerControl: PlayerControlView
    lateinit var albumImage: ImageView
    lateinit var title: TextView
    lateinit var artist: TextView

    companion object {
        var musicService : MusicService? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(intent)
        else
            startService(intent)

        albumImage = findViewById(R.id.album_portada)
        title = findViewById(R.id.cancion)
        artist = findViewById(R.id.artista)
        playerControl = findViewById(R.id.playerControl)
    }

    private fun createPlayer() {
        if (MusicService.exoPlayer == null) MusicService.exoPlayer = ExoPlayer.Builder(this).build()
        playerControl.player = MusicService.exoPlayer
        val songIndex = intent.getIntExtra("songIndex", 0)

        updateView(MusicDirectoryFragment.mediaItemsForPlayer[songIndex])
        MusicService.exoPlayer!!.setMediaItems(MusicDirectoryFragment.mediaItemsForPlayer)
        MusicService.exoPlayer!!.repeatMode = Player.REPEAT_MODE_ALL
        MusicService.exoPlayer!!.prepare()
        MusicService.exoPlayer!!.seekTo(songIndex, 0L)
        MusicService.exoPlayer!!.play()
    }

    private fun updateView(mediaItem: MediaItem){
        title.text = mediaItem.mediaMetadata.title
        artist.text = mediaItem.mediaMetadata.artist
        if (mediaItem.mediaMetadata.artworkUri != null)
            Glide.with(baseContext).load(mediaItem.mediaMetadata.artworkUri!!.toString()).into(albumImage)
        else
            albumImage.setImageResource(R.drawable.default_music)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        musicService = (service as MusicService.MyBinder).currentService()
        createPlayer()
        MusicService.exoPlayer!!.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                musicService!!.showNotification() //changes behaviour if notification does not update
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                if (mediaItem == null) finish()
                updateView(mediaItem!!)
                musicService!!.showNotification()
            }
        })
    }

    override fun onServiceDisconnected(name: ComponentName?) { musicService = null }
}