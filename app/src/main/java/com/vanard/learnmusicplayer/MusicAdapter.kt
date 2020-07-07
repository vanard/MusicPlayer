package com.vanard.learnmusicplayer

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vanard.learnmusicplayer.model.MusicFile
import java.io.File


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

        holder.menuMore.setOnClickListener {v ->
            val popupMenu = PopupMenu(context, v)
            popupMenu.menuInflater.inflate(R.menu.pop_music_file, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.pop_delete -> deleteFile(position, v)
                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, PlayerActivity::class.java)
                    .putExtra("pos", position)
            )
        }

    }

    private fun deleteFile(position: Int, v: View): Boolean {
        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mFiles[position].id!!.toLong())
        val file = File(mFiles[position].path)
        val deleted = file.delete()
        if (deleted) {
            context.contentResolver.delete(contentUri, null, null)
            mFiles.removeAt(position)
            notifyItemRemoved(position)
            Snackbar.make(v, "File deleted : ${mFiles[position].title}", Snackbar.LENGTH_LONG).show()
        }else {
            Snackbar.make(v, "File can't be deleted : ", Snackbar.LENGTH_LONG).show()
        }

        return deleted
    }

    class MusicHolder(item: View) : RecyclerView.ViewHolder(item) {
        val albumArt : ImageView = item.findViewById(R.id.musicImg)
        val title : TextView = item.findViewById(R.id.musicFileName)
        val artist : TextView = item.findViewById(R.id.musicArtistName)
        val menuMore : ImageView = item.findViewById(R.id.menuMore)
    }

    private fun getAlbumArt(uri: String?): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art = retriever.embeddedPicture
        retriever.release()
        return art
    }

}