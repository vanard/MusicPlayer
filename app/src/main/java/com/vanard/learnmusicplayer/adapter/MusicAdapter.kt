package com.vanard.learnmusicplayer.adapter

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vanard.learnmusicplayer.R
import com.vanard.learnmusicplayer.model.MusicFile
import com.vanard.learnmusicplayer.ui.MainActivity.Companion.musicFile
import com.vanard.learnmusicplayer.ui.detail.PlayerActivity
import java.io.File


class MusicAdapter (private val mFiles: ArrayList<MusicFile>,
                    private val sender: String? = "") :
        RecyclerView.Adapter<MusicAdapter.MusicHolder>(), Filterable {

    private var mSongs = arrayListOf<MusicFile>()
    private lateinit var mContext: Context

    init {
        mSongs = mFiles
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.music_items, parent, false)
        mContext = parent.context
        return MusicHolder(view)
    }

    override fun getItemCount(): Int {
        return mSongs.size
    }

    override fun onBindViewHolder(holder: MusicHolder, position: Int) {
        val mFile: MusicFile = mSongs[position]
        holder.title.text = mFile.title
        holder.artist.text = mFile.artist

        val img : ByteArray? = getAlbumArt(mFile.path)
        if (img != null) {
            Glide.with(mContext).asBitmap().load(img).into(holder.albumArt)

        } else {
            Glide.with(mContext).asBitmap()
                .load(R.drawable.ic_music_note)
                .into(holder.albumArt)
        }

        holder.menuMore.setOnClickListener {v ->
            val popupMenu = PopupMenu(mContext, v)
            popupMenu.menuInflater.inflate(R.menu.pop_music_file, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.delete_popmenu -> deleteFile(position, v)
                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, PlayerActivity::class.java)
                    .putExtra("pos", position)
                    .putExtra("sender", sender)
            )
        }

    }

    private fun deleteFile(position: Int, v: View): Boolean {
        val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            mSongs[position].id!!.toLong())
        val file = File(mSongs[position].path!!)
        val deleted = file.delete()
        if (deleted) {
            mContext.contentResolver.delete(contentUri, null, null)
            mSongs.removeAt(position)
            notifyItemRemoved(position)
            Snackbar.make(v, "File deleted : ${mSongs[position].title}", Snackbar.LENGTH_LONG).show()
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

    companion object {
        fun getAlbumArt(uri: String?): ByteArray? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            retriever.release()
            return art
        }
    }

    fun setNewData(newSongs: ArrayList<MusicFile>) {
        mSongs = newSongs
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter = fileFilter()

    private fun fileFilter() : Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterPattern = constraint.toString().toLowerCase().trim()
            mSongs = if (constraint == null || constraint.isEmpty()) {
                mFiles
            } else {
                val filteredFile = arrayListOf<MusicFile>()
                for (item in mFiles) {
                    if (item.title!!.toLowerCase().contains(filterPattern))
                        filteredFile.add(item)
                }
                filteredFile
            }

            val res = FilterResults()
            res.values = mSongs
            return res
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            musicFile = mSongs
            mSongs = results?.values as ArrayList<MusicFile>
            notifyDataSetChanged()
        }

    }




}