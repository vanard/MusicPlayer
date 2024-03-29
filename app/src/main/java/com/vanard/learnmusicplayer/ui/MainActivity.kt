package com.vanard.learnmusicplayer.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.ViewPagerAdapter
import com.vanard.learnmusicplayer.databinding.ActivityMainBinding
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.main.AlbumsFragment
import com.vanard.learnmusicplayer.ui.main.SongsFragment
import com.vanard.learnmusicplayer.util.Constants
import com.vanard.learnmusicplayer.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private var sortBy = Constants.PREFERENCES_SORT_BY_NAME

    private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2
    )

    private val TAG = "MainActivity"
    private val WRITE_REQUEST_CODE = 101

    companion object {
        var musicFile : ArrayList<MusicFile> = arrayListOf()
        var shuffleBoolean = false
        var repeatBoolean = false
        var repeatOneBoolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)
        
        permission()
        setupViewPager()
    }

    override fun onResume() {
//        permission()
        super.onResume()
    }

    private fun setupViewPager() {
        val fragmentList = arrayListOf<Fragment>(
            SongsFragment(),
            AlbumsFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabs, binding.viewPager,
            TabConfigurationStrategy { tab, position ->
                tab.text = this.resources.getString(TAB_TITLES[position])
            }).attach()
    }

    private fun permission() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        } else {
            mainViewModel.readSelectedSortBy.observe(this, {
                sortBy = it
                musicFile = getAllAudio(this, sortBy)
            })
//            musicFile = getAllAudio(this, sortBy)

        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            WRITE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            WRITE_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    musicFile = getAllAudio(this, sortBy)

                } else {
                    makeRequest()
                }

            }
        }
    }

    private fun getAllAudio(context: Context, sortBy: String): ArrayList<MusicFile> {
        val tempAudioList: ArrayList<MusicFile> = ArrayList()
        val mOrder = when(sortBy) {
            Constants.PREFERENCES_SORT_BY_NAME -> MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
            Constants.PREFERENCES_SORT_BY_DATE -> MediaStore.MediaColumns.DATE_ADDED + " ASC"
            Constants.PREFERENCES_SORT_BY_SIZE -> MediaStore.MediaColumns.SIZE + " DESC"
            else -> MediaStore.MediaColumns.DISPLAY_NAME + " ASC"
        }

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media._ID
        )
        Log.d(TAG, "getAllAudio: $uri")
        val cursor: Cursor? = context.contentResolver.query(
            uri, projection, null, null, mOrder
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val album = cursor.getString(0)
                val title = cursor.getString(1)
                val duration = cursor.getString(2)
                val path = cursor.getString(3)
                val artist = cursor.getString(4)
                val id = cursor.getString(5)

                val musicFile = MusicFile(path, title, artist, album, duration, id)

                //check
                Log.i("path: $path", "album: $album")

                tempAudioList.add(musicFile)
            }

            cursor.close()
        }

        return tempAudioList
    }

}