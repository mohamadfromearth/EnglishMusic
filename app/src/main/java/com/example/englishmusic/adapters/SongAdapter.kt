package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.model.song.SongItem

class SongAdapter(
    private val layout:Int,
    private val status:Int,
): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {



    inner class SongViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<SongItem>() {
        override fun areItemsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
           return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: SongItem, newItem: SongItem): Boolean {
            return oldItem._id == newItem._id
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            LayoutInflater.from(parent.context).inflate(
                layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = differ.currentList[position]
        holder.itemView.apply {
            val coverImg = findViewById<ImageView>(R.id.songCover)
            val nameOfSong = findViewById<TextView>(R.id.nameOfSong)
            val nameOfArtist = findViewById<TextView>(R.id.nameOfArtist)


            nameOfSong.text = song.name
            nameOfArtist.text = song.artist
            Glide.with(this).load(song.cover).into(coverImg)

            setOnClickListener {
                setOnItemClickListener?.let {
                    it(song)
                }
            }
            if (layout==R.layout.item_songs){
                val option = findViewById<ImageView>(R.id.options)
                option.setOnClickListener {
                    setOnOptionClickLisener?.let {
                        it(song)
                    }
            }

            }

        }

    }

    override fun getItemCount(): Int {
        if (status==0){
            return  differ.currentList.size
        }
        return 5

    }

    val differ = AsyncListDiffer(this,differCallback)

    private var setOnItemClickListener:((SongItem) -> Unit)? = null
    fun setOnItemClick(listener:(SongItem)->Unit){
        setOnItemClickListener = listener
    }
    private var setOnOptionClickLisener:((SongItem)->Unit)?=null

    fun setOnOptionClick(listener:(SongItem)->Unit){
        setOnOptionClickLisener = listener
    }


}