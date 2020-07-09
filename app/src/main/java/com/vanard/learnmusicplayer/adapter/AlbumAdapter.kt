package com.vanard.learnmusicplayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.adapter.MusicAdapter.Companion.getAlbumArt
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.detail.AlbumDetailActivity

class AlbumAdapter (private val mFiles: ArrayList<MusicFile>, private val mContext: Context) :
    RecyclerView.Adapter<AlbumAdapter.AlbumHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_items, parent, false)
        return AlbumHolder(view)
    }

    override fun getItemCount(): Int {
        return mFiles.size
    }

    override fun onBindViewHolder(holder: AlbumHolder, position: Int) {
        val mFile: MusicFile = mFiles[position]
        val img : ByteArray? = getAlbumArt(mFile.path)
        if (mFile.album == "" || mFile.album == "-")
            holder.albumName.text = mContext.getString(R.string.unknown_text)
        else holder.albumName.text = mFile.album

        if (img != null) {
            Glide.with(mContext).asBitmap().load(img).into(holder.albumArt)

        } else {
            Glide.with(mContext).asBitmap()
                .load(R.drawable.ic_music_note)
                .into(holder.albumArt)
        }

        holder.itemView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, AlbumDetailActivity::class.java)
                    .putExtra("albumName", mFile.album)
            )
        }
    }

    class AlbumHolder (item: View) : RecyclerView.ViewHolder(item) {
        val albumArt : ImageView = item.findViewById(R.id.album_image)
        val albumName : TextView = item.findViewById(R.id.album_name)
    }

}