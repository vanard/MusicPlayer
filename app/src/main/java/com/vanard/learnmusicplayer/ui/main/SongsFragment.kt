package com.vanard.learnmusicplayer.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile

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

        setHasOptionsMenu(true)

        musicAdapter = MusicAdapter(
            musicFile
        )

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchAction = menu.findItem(R.id.action_search)
        val searchView = SearchView(requireContext())
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                musicAdapter.filter.filter(newText)
                return true
            }

        })
        
        searchAction.actionView = searchView
        super.onCreateOptionsMenu(menu, inflater)
    }

}