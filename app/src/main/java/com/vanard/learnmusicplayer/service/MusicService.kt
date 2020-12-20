package com.vanard.learnmusicplayer.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.vanard.learnmusicplayer.model.MusicFile

class MusicService: Service() {

    private var mBinder: IBinder = MyBinder()
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var musicFiles: ArrayList<MusicFile> = arrayListOf()

    override fun onBind(p0: Intent?): IBinder {
        Log.d("Music Service", "onBind: Method")
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    class MyBinder : Binder() {
        internal val service : MusicService
            get() {
                return MusicService()
            }
    }
}