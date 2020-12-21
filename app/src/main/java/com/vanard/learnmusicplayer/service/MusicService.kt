package com.vanard.learnmusicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.ActionPlaying
import com.vanard.learnmusicplayer.ui.detail.PlayerActivity.Companion.listSongs
import com.vanard.learnmusicplayer.util.Constants.Companion.SERVICE_POSITION

class MusicService: Service(), MediaPlayer.OnCompletionListener {

    private var mBinder: IBinder = MyBinder()
    private var musicFiles: ArrayList<MusicFile> = arrayListOf()
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var uri: Uri
    private lateinit var actionPlaying: ActionPlaying
    private var position = -1

    override fun onCreate() {
        super.onCreate()
//        if (!listSongs.isNullOrEmpty())

    }

    override fun onBind(p0: Intent?): IBinder {
        Log.d("Music Service", "onBind: Method")
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val myPosition = intent.getIntExtra(SERVICE_POSITION, -1)
        if (myPosition != -1) {
            playMedia(myPosition)
        }
        playMedia(position)
        return START_STICKY
    }

    private fun playMedia(startPosition: Int) {
        musicFiles = listSongs!!
        position = startPosition
        if (mediaPlayer != null) {
            mediaPlayer.stop()
            mediaPlayer.release()
            if (musicFiles != null) {
                createMediaPlayer(position)
                mediaPlayer.start()
            }
        }else {
            createMediaPlayer(position)
            mediaPlayer.start()
        }
    }


    class MyBinder : Binder() {
        internal val service : MusicService
            get() {
                return MusicService()
            }
    }

    fun start() {
        mediaPlayer.start()
    }

    fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    fun stop() {
        mediaPlayer.stop()
    }

    fun reset() {
        mediaPlayer.reset()
    }

    fun release() {
        mediaPlayer.release()
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun createMediaPlayer(position: Int) {
        uri = Uri.parse(
            listSongs!![position].path)
        mediaPlayer = MediaPlayer.create(baseContext, uri)
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun onCompleted() {
        mediaPlayer.setOnCompletionListener(this)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if (actionPlaying != null)
            actionPlaying.btnNextClicked()

        createMediaPlayer(position)
        mediaPlayer.start()
        onCompleted()
    }
}