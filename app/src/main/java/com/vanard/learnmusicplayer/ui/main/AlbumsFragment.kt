package com.vanard.learnmusicplayer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.AlbumAdapter
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import java.util.*

class AlbumsFragment : Fragment() {

    private lateinit var albumAdapter: AlbumAdapter
    private var album: ArrayList<MusicFile> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_albums, container, false)
        val rvAlbum: RecyclerView = view.findViewById(R.id.rvAlbum)

        val albumList = musicFile.distinctBy { it.album }
        albumList.forEach {
            album.add(it)
        }

        albumAdapter = AlbumAdapter(
            album,
            requireContext()
        )

        rvAlbum.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
            adapter = albumAdapter
        }

        return view
    }

}