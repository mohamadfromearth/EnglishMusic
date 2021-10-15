package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.englishmusic.R
import com.example.englishmusic.model.DownloadSong

class DownloadAdapter: RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder>() {

    inner class DownloadViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<DownloadSong>() {
        override fun areItemsTheSame(oldItem: DownloadSong, newItem: DownloadSong): Boolean {
        return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DownloadSong, newItem: DownloadSong): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {

       return DownloadViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_songs,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        val song = differ.currentList[position]
        holder.itemView.apply {
            val songImg = findViewById<ImageView>(R.id.songCover)
            val songName = findViewById<TextView>(R.id.nameOfSong)
            val option = findViewById<ImageView>(R.id.options)
            songImg.setImageBitmap(song.songImg)
            songName.text = song.songName
            setOnClickListener {
                onClickListener?.let {
                    it(song)
                }
            }
            option.setOnClickListener {
                onOptionClickListener?.let {
                    it(song)
                }
            }
        }
    }

    private var onClickListener:((DownloadSong)->Unit)? = null
    fun setOnClickListener(listener:(DownloadSong)->Unit){
         onClickListener = listener
    }

    private var onOptionClickListener:((DownloadSong)->Unit)? = null
    fun setOnOptionClickListener(listener:(DownloadSong)->Unit){
        onOptionClickListener = listener
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    val differ = AsyncListDiffer(this,differCallback)
}