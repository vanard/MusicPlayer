package com.vanard.learnmusicplayer.ui.detail

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.databinding.ActivityPlayerBinding
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.service.MusicService
import com.vanard.learnmusicplayer.ui.ActionPlaying
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.repeatBoolean
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.repeatOneBoolean
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.shuffleBoolean
import com.vanard.learnmusicplayer.ui.detail.AlbumDetailActivity.Companion.albumSongs
import java.util.*


class PlayerActivity : AppCompatActivity(),
    ActionPlaying, ServiceConnection{

    private lateinit var binding: ActivityPlayerBinding

    private val TAG = "PlayerActivity"
    
    private var pos = -1
    private var musicHandler = Handler(Looper.getMainLooper())
    private lateinit var playThread : Thread
    private lateinit var nextThread : Thread
    private lateinit var prevThread : Thread
    private var musicService: MusicService? = null

    companion object {
        var listSongs : ArrayList<MusicFile>? = arrayListOf()
        lateinit var uri : Uri
//        var mediaPlayer : MediaPlayer? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupView()

        runOnUiThread(object : Runnable {
            override fun run() {
                if (musicService != null) {
                    val mCurrentPos = musicService!!.getCurrentPosition() / 1000
                    binding.seekBar.progress = mCurrentPos
                    binding.durationPlayed.text = formattedTime(mCurrentPos)
                }
                musicHandler.postDelayed(this, 1000)
            }
        })

    }

    private fun setupView() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (musicService != null && fromUser) {
                    musicService!!.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }

        })
        binding.shuffleBtn.setOnClickListener {
            if (shuffleBoolean) {
                shuffleBoolean = false
                binding.shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_24)
            } else {
                shuffleBoolean = true
                binding.shuffleBtn.setImageResource(R.drawable.ic_baseline_shuffle_on_24)
            }
        }
        binding.repeatBtn.setOnClickListener {
            if (repeatBoolean && repeatOneBoolean) {
                repeatBoolean = false
                repeatOneBoolean = false
                binding.repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_24)
            } else if (repeatBoolean && !repeatOneBoolean) {
                repeatOneBoolean = true
                binding.repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on_one_24)
            }
            else {
                repeatBoolean = true
                binding.repeatBtn.setImageResource(R.drawable.ic_baseline_repeat_on_24)
            }
        }
    }

    override fun onResume() {
        val intent = Intent(this, MusicService::class.java)
        bindService(intent, this, BIND_AUTO_CREATE)

        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        unbindService(this)
    }

    private fun prevThreadBtn() {
        prevThread = object : Thread() {
            override fun run() {
                super.run()
                binding.prevBtn.setOnClickListener {
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
                binding.nextBtn.setOnClickListener {
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
                binding.playPauseBtn.setOnClickListener {
                    playPauseBtnClicked()
                }
            }
        }
        playThread.start()
    }

    private fun prevBtnClicked() {
        if (musicService == null) return

        if (musicService!!.isPlaying()) {
            preparePrevSong()
            binding.playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            musicService!!.start()
        } else {
            preparePrevSong()
            binding.playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun nextBtnClicked() {
        if (musicService == null) return

        if (musicService!!.isPlaying()) {
            prepareNextSong()
            binding.playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_pause_24)
            musicService!!.start()
        } else {
            prepareNextSong()
            binding.playPauseBtn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }

    private fun playPauseBtnClicked() {
        if (musicService == null) return

        if (musicService!!.isPlaying()) {
            binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_play_arrow_24)
            musicService!!.pause()
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
            binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            musicService!!.start()
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
        musicService!!.createMediaPlayer(pos)
        metadataR(uri, 2)
        setDisplayMusic()
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
        musicService!!.createMediaPlayer(pos)
        metadataR(uri, 1)
        setDisplayMusic()
    }

    private fun setDisplayMusic() {
        binding.songName.text = listSongs!![pos].title
        binding.artistName.text = listSongs!![pos].artist
        binding.seekBar.max = musicService!!.getDuration() / 1000
        musicService!!.onCompleted()
    }

    private fun getRandom(i: Int): Int {
        val random = Random()
        return random.nextInt(i + 1)
    }

    private fun stopMp() {
        musicService!!.stop()
        musicService!!.reset()
        musicService!!.release()
    }

    private fun formattedTime(mCurrentPos : Int) : String {
//        var totalOut = ""
//        var totalNew = ""
        val seconds : String = (mCurrentPos % 60).toString()
        val minutes : String = (mCurrentPos / 60).toString()
        val totalOut = "$minutes:$seconds"
        val totalNew = "$minutes:0$seconds"

        return if (seconds.length == 1) totalNew
        else totalOut
    }

    private fun getIntentData() {
        pos = intent.getIntExtra("pos", -1)
        val sender = intent.getStringExtra("sender")
        listSongs = if (sender == "albumDetail") albumSongs
            else musicFile

        if (listSongs != null) {
            binding.playPauseBtn.setImageResource(R.drawable.ic_baseline_pause_24)
            uri = Uri.parse(
                listSongs!![pos].path) // fromFile
            binding.songName.text = listSongs!![pos].title
            binding.artistName.text = listSongs!![pos].artist
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (musicService != null) {
            stopMp()
            musicService!!.createMediaPlayer(pos)
            musicService!!.start()
        } else {
            musicService!!.createMediaPlayer(pos)
            musicService!!.start()
        }

        musicService!!.onCompleted()

    }

    private fun metadataR(uri: Uri, code: Int) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri.toString())
        val mDurationTotal = Integer.parseInt(listSongs!![pos].duration!!) / 1000
        binding.durationTotal.text = formattedTime(mDurationTotal)
        val art : ByteArray? = retriever.embeddedPicture
        val bitmap: Bitmap?
        if (art != null) {
//            Glide.with(this).asBitmap().load(art).into(albumArtPlay)

            bitmap = BitmapFactory.decodeByteArray(art, 0, art.size)
            when (code) {
                0 -> imageAnimation(this, binding.albumArtPlay, bitmap)
                1 -> imageNextAnimation(this, binding.albumArtPlay, bitmap)
                2 -> imagePrevAnimation(this, binding.albumArtPlay, bitmap)
                else -> return
            }

        } else {
            Glide.with(this).asBitmap().load(R.drawable.ic_music_note).into(binding.albumArtPlay)

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

    override fun btnPlayPauseClicked() {
        playPauseBtnClicked()
    }

    override fun btnPrevClicked() {
        prevBtnClicked()
    }

    override fun btnNextClicked() {
        nextBtnClicked()
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val myBinder: MusicService.MyBinder = p1 as MusicService.MyBinder
        musicService = myBinder.service

        metadataR(uri, 0)
        binding.seekBar.max = musicService!!.getDuration() / 1000

        Toast.makeText(this, "Connected $musicService", Toast.LENGTH_SHORT).show()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        musicService = null
    }

}