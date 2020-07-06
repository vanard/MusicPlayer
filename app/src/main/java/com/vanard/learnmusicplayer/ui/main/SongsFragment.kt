package com.vanard.learnmusicplayer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.MainActivity
import com.vanard.learnmusicplayer.MusicAdapter
import com.vanard.learnmusicplayer.R

class SongsFragment : Fragment() {

    private val TAG = "SongsFragment"
    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_songs, container, false)
        val rvMusic: RecyclerView = view.findViewById(R.id.rvMusic)

        musicAdapter = MusicAdapter(MainActivity.musicFile, requireContext())

        rvMusic.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = musicAdapter
        }

        return view
    }

    override fun onResume() {
//        musicAdapter.notifyDataSetChanged()
        super.onResume()
    }

}