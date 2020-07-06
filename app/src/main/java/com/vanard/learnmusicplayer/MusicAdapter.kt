package com.vanard.learnmusicplayer

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vanard.learnmusicplayer.model.MusicFile


class MusicAdapter (private val mFiles: ArrayList<MusicFile>, private val context: Context) :
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
        holder.artist.text = mFile.artist

        val img : ByteArray? = getAlbumArt(mFile.path)
        if (img != null) {
            Glide.with(context).asBitmap().load(img).into(holder.albumArt)

        } else {
            Glide.with(context).asBitmap()
                .load(R.drawable.ic_music_note)
                .into(holder.albumArt)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, PlayerActivity::class.java)
                    .putExtra("pos", position)
            )
        }

    }

    class MusicHolder(item: View) : RecyclerView.ViewHolder(item) {
        val albumArt : ImageView = item.findViewById(R.id.musicImg)
        val title : TextView = item.findViewById(R.id.musicFileName)
        val artist : TextView = item.findViewById(R.id.musicArtistName)
    }

    private fun getAlbumArt(uri: String?): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}