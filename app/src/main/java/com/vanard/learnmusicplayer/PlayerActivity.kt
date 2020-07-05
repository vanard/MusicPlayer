package com.vanard.learnmusicplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.model.MusicFile
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    private var pos = -1
    private var musicHandler = Handler()
    private lateinit var playThread : Thread
    private lateinit var nextThread : Thread
    private lateinit var prevThread : Thread

    companion object {
        var listSongs : ArrayList<MusicFile>? = arrayListOf()
        lateinit var uri : Uri
        var mediaPlayer : MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        getIntentData()

        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer!!.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }

        })

        runOnUiThread(object : Runnable {
            override fun run() {
                if (mediaPlayer != null) {
                    val mCurrentPos = mediaPlayer!!.currentPosition / 1000
                    seekBar.progress = mCurrentPos
                    durationPlayed.text = formattedTime(mCurrentPos)
                }
                musicHandler.postDelayed(this, 1000)
            }

        })

    }

    override fun onResume() {
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    private fun prevThreadBtn() {
        TODO("Not yet implemented")
    }

    private fun nextThreadBtn() {
        TODO("Not yet implemented")
    }

    private fun playThreadBtn() {
        playThread = object : Thread() {
            override fun run() {
                super.run()
                playPauseBtn.setOnClickListener {
                    playPauseBtnClicked()
                }
            }
        }
        playThread.start()
    }

    private fun playPauseBtnClicked() {
        TODO("Not yet implemented")
    }

    private fun formattedTime(mCurrentPos : Int) : String {
        var totalOut = ""
        var totalNew = ""
        val seconds : String = (mCurrentPos % 60).toString()
        val minutes : String = (mCurrentPos / 60).toString()
        totalOut = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"

        return if (seconds.length == 1) totalNew
        else totalOut
    }

    private fun getIntentData() {
        pos = intent.getIntExtra("pos", -1)
        listSongs = MainActivity.musicFile

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            uri = Uri.parse(listSongs!![pos].path)
            songName.text = listSongs!![pos].title
            artistName.text = listSongs!![pos].artist
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(applicationContext, uri)
            mediaPlayer!!.start()
        }

        metadataR(uri)
        seekBar.max = mediaPlayer!!.duration / 1000
    }

    private fun metadataR(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val mDurationTotal = Integer.parseInt(listSongs!![pos].duration!!) / 1000
        durationTotal.text = formattedTime(mDurationTotal)
        val art : ByteArray? = retriever.embeddedPicture
        if (art != null) {
            Glide.with(this).asBitmap().load(art).into(albumArtPlay)
        } else {
            Glide.with(this).asBitmap().load(R.drawable.ic_music_note).into(albumArtPlay)
        }
    }


}