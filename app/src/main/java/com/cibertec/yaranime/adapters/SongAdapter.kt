package com.cibertec.yaranime.adapters

import android.app.Application
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cibertec.yaranime.MusicPlayerActivity
import com.cibertec.yaranime.R
import com.cibertec.yaranime.models.Song


class SongAdapter(private val context: Context, private val items: List<Song>) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var songTitle: TextView
        var songArtist: TextView
        var albumArt: ImageView

        init {
            with(itemView){
                songTitle = findViewById(R.id.song_item_textViewSongTitle)
                songArtist = findViewById(R.id.song_item_textViewSongArtist)
                albumArt = findViewById(R.id.song_item_imageViewAlbumArt)
            }
        }

        fun bindData(data: Song) {
            songTitle.text = data.title
            songArtist.text = data.artist
            if (data.albumArtUri != null)
                Glide.with(context).load(data.albumArtUri).timeout(10000).into(albumArt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.itemView.setOnClickListener {

            val intent = Intent(context, MusicPlayerActivity::class.java)
            intent.putExtra("songIndex", position)

            ContextCompat.startActivity(context, intent, null)
        }
        holder.bindData(item)
    }
}