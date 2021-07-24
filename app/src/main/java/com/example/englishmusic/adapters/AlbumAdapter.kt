package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.model.AlbumItem
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class AlbumAdapter:RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {


    inner class AlbumViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    val differCallback = object : DiffUtil.ItemCallback<AlbumItem>(){
        override fun areItemsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
           return oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: AlbumItem, newItem: AlbumItem): Boolean {
            return oldItem.hashCode() == oldItem.hashCode()
        }

    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_album,parent,false
        ))
    }

    val differ = AsyncListDiffer(this,differCallback)

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = differ.currentList[position]
        holder.itemView.apply {
            val albumImg = findViewById<ShapeableImageView>(R.id.albumImg)
            val nameOfTheAlbum = findViewById<MaterialTextView>(R.id.nameOfAlbum)
            val timeOfTheReleased = findViewById<MaterialTextView>(R.id.timeOfTheReleased)

            nameOfTheAlbum.text = album.name
            timeOfTheReleased.text = album.released
            Glide.with(this).load(album.imageUrl).into(albumImg)

            setOnClickListener {
               onClickListener?.let {
                   it(album)
               }
            }



        }
    }

   private var onClickListener: ((AlbumItem) -> Unit)? = null
    fun setItemClickListener(listener:(AlbumItem)->Unit){
        onClickListener = listener
    }


    override fun getItemCount(): Int {
       return differ.currentList.size
    }

}