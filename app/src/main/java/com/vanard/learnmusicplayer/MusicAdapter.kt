package com.vanard.learnmusicplayer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vanard.learnmusicplayer.model.MusicFile

class MusicAdapter (private val mFiles: ArrayList<MusicFile>) :
        RecyclerView.Adapter<MusicAdapter.MusicHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_items, parent, false)

        return MusicHolder(view)
    }

    override fun getItemCount(): Int {
        return mFiles.size
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        val mFile: MusicFile = mFiles[position]
        holder.title.text = mFile.title
        holder.albumArt
    }

    class MusicHolder(item: View) : RecyclerView.ViewHolder(item) {
        val albumArt : ImageView = item.findViewById(R.id.musicImg)
        val title : TextView = item.findViewById(R.id.musicFileName)
    }

}