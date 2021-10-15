package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.model.PlaylistItem

class PlaylistAdapter:RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)


    private val differCallback = object : DiffUtil.ItemCallback<PlaylistItem>() {
        override fun areItemsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
          return  oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: PlaylistItem, newItem: PlaylistItem): Boolean {
           return oldItem.hashCode() == newItem.hashCode()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_playlists,parent,false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {

        holder.itemView.apply {
            val playlist = differ.currentList[position]
            val playListImg = findViewById<ImageView>(R.id.playlistImg)
            Glide.with(this).load(playlist.imageUrl).into(playListImg)
            setOnClickListener {
                onItemClicklistener?.let {
                    it(playlist)
                }
            }

        }
    }

    private var onItemClicklistener:((PlaylistItem)->Unit)? = null
    fun setOnItemClickListener(listener:(PlaylistItem)-> Unit){
    onItemClicklistener = listener
    }




    val differ = AsyncListDiffer(this,differCallback)

    override fun getItemCount(): Int {

       return differ.currentList.size
    }
}