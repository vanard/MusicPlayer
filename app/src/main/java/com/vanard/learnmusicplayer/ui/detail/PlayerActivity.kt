package com.vanard.learnmusicplayer.ui.detail

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.repeatBoolean
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.repeatOneBoolean
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.shuffleBoolean
import com.vanard.learnmusicplayer.ui.detail.AlbumDetailActivity.Companion.albumSongs
import kotlinx.android.synthetic.main.activity_player.*
import java.util.*


class PlayerActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener {

    private val TAG = "PlayerActivity"
    
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
        setupView()

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

    private fun setupView() {
        backBtn.setOnClickListener {
            onBackPressed()
        }

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
        shuffleBtn.setOnClickListener {
            if (shuffleBoolean) {
                shuffleBoolean = false
                shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_24)
            } else {
                shuffleBoolean = true
                shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on_24)
            }
        }
        repeatBtn.setOnClickListener {
            if (repeatBoolean && repeatOneBoolean) {
                repeatBoolean = false
                repeatOneBoolean = false
                repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_24)
            } else if (repeatBoolean && !repeatOneBoolean) {
                repeatOneBoolean = true
                repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on_one_24)
            }
            else {
                repeatBoolean = true
                repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on_24)
            }
        }
    }

    override fun onResume() {
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                prevBtn.setOnClickListener {
                    prevBtnClicked()
                }
            }
        }
        prevThread.start()
    }

    private fun nextThreadBtn() {
        nextThread = object : Thread() {
            override fun run() {
                super.run()
                nextBtn.setOnClickListener {
                    nextBtnClicked()
                }
            }
        }
        nextThread.start()
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

    private fun prevBtnClicked() {
        if (mediaPlayer == null) return

        if (mediaPlayer!!.isPlaying) {
            preparePrevSong()
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
        } else {
            preparePrevSong()
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun nextBtnClicked() {
        if (mediaPlayer == null) return

        if (mediaPlayer!!.isPlaying) {
            prepareNextSong()
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
        } else {
            prepareNextSong()
            playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun playPauseBtnClicked() {
//        Log.d(TAG, "playPauseBtnClicked: true")
        if (mediaPlayer == null) return

        if (mediaPlayer!!.isPlaying) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            mediaPlayer!!.pause()
//            seekBar.max = mediaPlayer!!.duration / 1000
//            runOnUiThread(object : Runnable {
//                override fun run() {
//
//                    val mCurrentPos = mediaPlayer!!.currentPosition / 1000
//                    seekBar.progress = mCurrentPos
//
//                    musicHandler.postDelayed(this, 1000)
//                }
//            })
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer!!.start()
        }
    }

    private fun preparePrevSong() {
        stopMp()
//        if (!repeatBoolean) listSongs!!.removeAt(pos)
        if (shuffleBoolean && !repeatBoolean) {
            pos = getRandom(listSongs!!.size - 1)
        } else if (!shuffleBoolean && !repeatBoolean) {
            pos = if (pos - 1 < 0) listSongs!!.size - 1 else pos - 1
        }
        uri = Uri.parse(
            listSongs!![pos].path) // fromFile
        mediaPlayer = MediaPlayer.create(applicationContext,
            uri
        )
        metadataR(uri, 2)
        songName.text = listSongs!![pos].title
        artistName.text = listSongs!![pos].artist
        seekBar.max = mediaPlayer!!.duration / 1000
        mediaPlayer!!.setOnCompletionListener(this)
    }

    private fun prepareNextSong() {
        stopMp()
//        if (!repeatBoolean ) listSongs!!.removeAt(pos)
        if (shuffleBoolean && !repeatBoolean) {
            pos = getRandom(listSongs!!.size - 1)
        } else if (!shuffleBoolean && !repeatBoolean) {
            pos = (pos + 1) % listSongs!!.size
        }
        // else
        uri = Uri.parse(
            listSongs!![pos].path) // fromFile
        mediaPlayer = MediaPlayer.create(applicationContext,
            uri
        )
        metadataR(uri, 1)
        songName.text = listSongs!![pos].title
        artistName.text = listSongs!![pos].artist
        seekBar.max = mediaPlayer!!.duration / 1000
        mediaPlayer!!.setOnCompletionListener(this)
    }

    private fun getRandom(i: Int): Int {
        val random = Random()
        return random.nextInt(i + 1)
    }

    private fun stopMp() {
        mediaPlayer!!.stop()
        mediaPlayer!!.reset()
        mediaPlayer!!.release()
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
        val sender = intent.getStringExtra("sender")
        listSongs = if (sender == "albumDetail") albumSongs
            else musicFile

        if (listSongs != null) {
            playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            uri = Uri.parse(
                listSongs!![pos].path) // fromFile
            songName.text = listSongs!![pos].title
            artistName.text = listSongs!![pos].artist
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (mediaPlayer != null) {
            stopMp()
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            mediaPlayer!!.start()
        } else {
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            mediaPlayer!!.start()
        }

        mediaPlayer!!.setOnCompletionListener(this)
        metadataR(uri, 0)
        seekBar.max = mediaPlayer!!.duration / 1000
    }

    private fun metadataR(uri: Uri, code: Int) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val mDurationTotal = Integer.parseInt(listSongs!![pos].duration!!) / 1000
        durationTotal.text = formattedTime(mDurationTotal)
        val art : ByteArray? = retriever.embeddedPicture
        var bitmap : Bitmap? = null
        if (art != null) {
//            Glide.with(this).asBitmap().load(art).into(albumArtPlay)

            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            when (code) {
                0 -> imageAnimation(this, albumArtPlay, bitmap)
                1 -> imageNextAnimation(this, albumArtPlay, bitmap)
                2 -> imagePrevAnimation(this, albumArtPlay, bitmap)
                else -> return
            }

        } else {
            Glide.with(this).asBitmap().load(R.drawable.ic_music_note).into(albumArtPlay)

//            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
//            imageAnimation(this, albumArtPlay, bitmap)
        }
    }

    fun imageAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        val animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                //
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                imageView.startAnimation(animIn)
            }

            override fun onAnimationStart(animation: Animation?) {
                //
            }

        })
        imageView.startAnimation(animOut)
    }

    fun imageNextAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context,
            R.anim.slide_out_left
        )
        val animIn = AnimationUtils.loadAnimation(context,
            R.anim.slide_in_left
        )
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                //
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                imageView.startAnimation(animIn)
            }

            override fun onAnimationStart(animation: Animation?) {
                //
            }

        })
        imageView.startAnimation(animOut)
    }

    fun imagePrevAnimation(context: Context, imageView: ImageView, bitmap: Bitmap) {
        val animOut = AnimationUtils.loadAnimation(context,
            R.anim.slide_out_right
        )
        val animIn = AnimationUtils.loadAnimation(context,
            R.anim.slide_in_right
        )
        animOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
                //
            }

            override fun onAnimationEnd(animation: Animation?) {
                Glide.with(context).load(bitmap).into(imageView)
                imageView.startAnimation(animIn)
            }

            override fun onAnimationStart(animation: Animation?) {
                //
            }

        })
        imageView.startAnimation(animOut)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        nextBtnClicked()
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(applicationContext,
                uri
            )
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener(this)
        }
    }

}