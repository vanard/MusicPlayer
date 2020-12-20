package com.vanard.learnmusicplayer.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter
import com.vanard.learnmusicplayer.databinding.FragmentSongsBinding
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_SORT_BY_DATE
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_SORT_BY_NAME
import com.vanard.learnmusicplayer.util.Constants.Companion.PREFERENCES_SORT_BY_SIZE
import com.vanard.learnmusicplayer.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsFragment : Fragment(), SearchView.OnQueryTextListener {

    private val TAG = "SongsFragment"

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var musicAdapter: MusicAdapter

    private var sortBy = PREFERENCES_SORT_BY_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
    }

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

        mainViewModel.readSelectedSortBy.observe(viewLifecycleOwner, {
            sortBy = it
            musicAdapter.setNewData(musicFile)
        })

        binding.rvMusic.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = musicAdapter
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchAction = menu.findItem(R.id.search_menu)
        val searchView = searchAction.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
        searchView?.queryHint = "Search..."

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.sort_name -> mainViewModel.saveSelectedSortBy(PREFERENCES_SORT_BY_NAME)
            R.id.sort_date -> mainViewModel.saveSelectedSortBy(PREFERENCES_SORT_BY_DATE)
            R.id.sort_size -> mainViewModel.saveSelectedSortBy(PREFERENCES_SORT_BY_SIZE)
        }
        return super.onOptionsItemSelected(item)
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