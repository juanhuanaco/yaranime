package com.cibertec.yaranime.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.cibertec.yaranime.R
import com.cibertec.yaranime.models.Cancion
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MusicFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MusicFragment : Fragment(), Player.Listener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    // VARIABLES DE REPRODUCTOR
    lateinit var player: ExoPlayer
    lateinit var playerControl: PlayerControlView
    // VARIABLES DE CONEXION A FIREBASE
    val database = Firebase.database
    val myRef = database.getReference("canciones")
    // VARIABLE TIPO ARRAYLIST PARA RECIBIR NUESTRAS CANCIONES DE FIREBASE
    lateinit var cancionItems:ArrayList<MediaItem>
    // VARIABLES PARA CAMBIAR LOS METADATOS SEGUN LA CANCION
    lateinit var albumImage:ImageView
    lateinit var title:TextView
    lateinit var album:TextView
    lateinit var artist:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_music, container, false)
        albumImage = view.findViewById(R.id.album_portada)
        title = view.findViewById(R.id.cancion)
        album = view.findViewById(R.id.album)
        artist = view.findViewById(R.id.artista)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerControl = view.findViewById(R.id.playerControl)
        cancionItems = ArrayList()
        getCanciones()
    }
    private fun getCanciones() {
        myRef.get().addOnSuccessListener {
            for(data in it.children){
                var auxSong = data.getValue(Cancion::class.java)
                if(auxSong!=null){
                    var metadata = MediaMetadata.Builder()
                        .setTitle(auxSong.title)
                        .setArtist(auxSong.artist)
                        .setAlbumTitle(auxSong.album)
                        .setArtworkUri(Uri.parse(auxSong.img))
                        .build()
                    var item:MediaItem = MediaItem.Builder()
                        .setUri(auxSong.song_url)
                        .setMediaMetadata(metadata)
                        .build()
                    cancionItems.add(item)
                }
            }
            iniciarPlayer()
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }
    private fun iniciarPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        player.repeatMode = Player.REPEAT_MODE_ALL
        playerControl.player = player
        player.addMediaItems(cancionItems);
        player.addListener(this)
        player.prepare()
    }
    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (mediaItem!=null) {
            Picasso.get().load(mediaItem.mediaMetadata.artworkUri).into(albumImage);
            title.text = mediaItem.mediaMetadata.title
            album.text = mediaItem.mediaMetadata.albumTitle
            artist.text = mediaItem.mediaMetadata.artist
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MusicFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MusicFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}