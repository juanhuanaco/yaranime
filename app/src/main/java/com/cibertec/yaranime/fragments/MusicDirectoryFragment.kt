package com.cibertec.yaranime.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.database.getStringOrNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.yaranime.R
import com.cibertec.yaranime.adapters.SongAdapter
import com.cibertec.yaranime.models.Song
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

enum class SongSrc {FROM_LOCAL, FROM_FIREBASE}

class MusicDirectoryFragment : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private val myRef = Firebase.database.getReference("canciones")
    private lateinit var tabLayout: TabLayout
    private lateinit var requestPermission : ActivityResultLauncher<String>
    private var songsFromFirebase : ArrayList<Song>? = null
    private var mediaItemsFromFirebase : ArrayList<MediaItem>? = null
    private var mediaItemsFromLocal : ArrayList<MediaItem>? = null
    private var songsFromLocal : ArrayList<Song>? = null

    companion object {
        @JvmStatic
        fun newInstance() =
            MusicDirectoryFragment().apply {
                arguments = Bundle()
            }
        var songs = ArrayList<Song>()
        lateinit var mediaItemsForPlayer : List<MediaItem>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { requestStoragePermission() }
        return inflater.inflate(R.layout.fragment_music_directory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.fragment_music_directory_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        tabLayout = view.findViewById(R.id.TabLayoutSongSrc)
        tabLayout.addOnTabSelectedListener (
            object: TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) { requestStoragePermission() }
                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        requestStoragePermission()

    }

    private fun updateRecyclerView() {
        recyclerView.adapter = SongAdapter(requireContext(), songs)
    }

    private fun getSelectedSongSrc() : SongSrc {
        return if (tabLayout.selectedTabPosition == 0) SongSrc.FROM_FIREBASE else SongSrc.FROM_LOCAL
    }

    private fun requestStoragePermission(){
        if (getSelectedSongSrc() == SongSrc.FROM_FIREBASE || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            getSongs()
        else
            requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun getSongs(){
        songs.clear()
        if (getSelectedSongSrc() == SongSrc.FROM_LOCAL) {
            songsFromLocal = songsFromLocal ?: getSongsFromLocal()
            songs = songsFromLocal!!.clone() as ArrayList<Song>
            mediaItemsFromLocal = mediaItemsFromLocal ?: convertSongsToMediaItems(songs)
            mediaItemsForPlayer = mediaItemsFromLocal!!.toList()
        }
        else {
            if (songsFromFirebase == null) setSongsFromFirebase()
            else {
                songs = songsFromFirebase!!.clone() as ArrayList<Song>
                mediaItemsFromFirebase = mediaItemsFromFirebase ?: convertSongsToMediaItems(songs)
                mediaItemsForPlayer = mediaItemsFromFirebase!!.toList()
            }

        }
        updateRecyclerView()
    }

    private fun convertSongsToMediaItems(songs : ArrayList<Song>) : ArrayList<MediaItem>{
        val mediaItems = ArrayList<MediaItem>()
        songs.forEach {
            var metadataBuilder = MediaMetadata.Builder()
                .setTitle(it.title)
                .setArtist(it.artist)
            if (it.albumArtUri != null) metadataBuilder.setArtworkUri(Uri.parse(it.albumArtUri))

            mediaItems.add(MediaItem.Builder().setUri(it.songUri).setMediaMetadata(metadataBuilder.build()).build())
        }
        return mediaItems
    }

    private fun setSongsFromFirebase() {
        myRef.get().addOnSuccessListener {
            songsFromFirebase = ArrayList()
            for(data in it.children){
                var item = data.getValue(Song::class.java)
                songsFromFirebase!!.add(item!!)
            }
            getSongs()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }

    }

    private fun getSongsFromLocal() : ArrayList<Song> {
        val songsTemp = ArrayList<Song>()
        requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.DATA),
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            while (cursor.moveToNext()){
                val song = Song()
                song.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                song.artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                song.songUri = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))

                requireContext().contentResolver.query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.Albums.ALBUM_ART),
                    "${MediaStore.Audio.Albums._ID}=${cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))}",
                    null,
                    null
                )?.use {
                    while (it.moveToNext()){
                        song.albumArtUri = it.getStringOrNull(it.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART))
                    }
                }
                songsTemp.add(song)
            }
        }
        return songsTemp
    }


}