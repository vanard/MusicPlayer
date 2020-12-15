package com.vanard.learnmusicplayer.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter
import com.vanard.learnmusicplayer.databinding.FragmentSongsBinding
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile

class SongsFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "SongsFragment"

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicAdapter: MusicAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSongsBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        musicAdapter = MusicAdapter(
            musicFile
        )

        binding.rvMusic.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = musicAdapter
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchAction = menu.findItem(R.id.action_search)
        val searchView = searchAction.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        searchView?.queryHint = "Search..."

//        searchAction.actionView = searchView
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        musicAdapter.filter.filter(newText)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}