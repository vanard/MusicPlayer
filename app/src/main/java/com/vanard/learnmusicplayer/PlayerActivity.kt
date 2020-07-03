package com.vanard.learnmusicplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.vanard.learnmusicplayer.model.MusicFile
import kotlinx.android.synthetic.main.activity_player.*


class PlayerActivity : AppCompatActivity() {

    private var pos = -1
    private var handler = Handler()

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
                handler.postDelayed(this, 1000)
            }

        })

    }

    private fun formattedTime(mCurrentPos : Int) : String {
        var totalOut = ""
        var totalNew = ""
        val seconds : String = (mCurrentPos % 60).toString()
        val minutes : String = (mCurrentPos / 60).toString()
        totalOut = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"

        if (seconds.length == 1) return totalNew
        else return totalOut
    }

    private fun getIntentData() {
        pos = intent.getIntExtra("pos", -1)
        listSongs = MainActivity.musicFile

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            uri = Uri.parse(listSongs!![pos].path)
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

        seekBar.max = mediaPlayer!!.duration / 1000
    }
}