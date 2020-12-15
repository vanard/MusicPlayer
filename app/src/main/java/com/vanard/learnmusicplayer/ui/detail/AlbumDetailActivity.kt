package com.vanard.learnmusicplayer.ui.detail

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter
import com.vanard.learnmusicplayer.databinding.ActivityAlbumDetailBinding
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import java.util.*

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumDetailBinding

    private var albumName: String? = ""
    private var count = 0
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        var albumSongs: ArrayList<MusicFile> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        albumSongs.clear()
        albumName = intent.getStringExtra("albumName")
        for (i in 0 until musicFile.size) {
            if (albumName == musicFile[i].album) {
                albumSongs.add(musicFile[i])
                count++
            }
        }

        binding.albumName.text = albumName
        val img : ByteArray? = MusicAdapter.getAlbumArt(albumSongs[0].path)
        if (img != null) {
            Glide.with(this).asBitmap().load(img).into(binding.albumImage)

        } else {
            Glide.with(this).asBitmap()
                .load(R.drawable.ic_music_note)
                .into(binding.albumImage)
        }

        musicAdapter = MusicAdapter(
            albumSongs,
            "albumDetail"
        )

        binding.rvMusicAlbum.apply {
            layoutManager = LinearLayoutManager(this@AlbumDetailActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = musicAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}