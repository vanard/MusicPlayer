package com.vanard.learnmusicplayer.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import kotlinx.android.synthetic.main.activity_album_detail.*
import java.util.*

class AlbumDetailActivity : AppCompatActivity() {

    private var albumName: String? = ""
    private var count = 0
    private lateinit var musicAdapter: MusicAdapter

    companion object {
        var albumSongs: ArrayList<MusicFile> = arrayListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_detail)

        albumName = intent.getStringExtra("albumName")
        for (i in 0 until musicFile.size) {
            if (albumName == musicFile[i].album) {
                albumSongs.add(musicFile[i])
                count++
            }
        }

        album_name.text = albumName
        val img : ByteArray? = MusicAdapter.getAlbumArt(albumSongs[0].path)
        if (img != null) {
            Glide.with(this).asBitmap().load(img).into(album_image)

        } else {
            Glide.with(this).asBitmap()
                .load(R.drawable.ic_music_note)
                .into(album_image)
        }

        musicAdapter = MusicAdapter(
            albumSongs,
            this,
            "albumDetail"
        )

        rvMusicAlbum.apply {
            layoutManager = LinearLayoutManager(this@AlbumDetailActivity, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            adapter = musicAdapter
        }
    }
}