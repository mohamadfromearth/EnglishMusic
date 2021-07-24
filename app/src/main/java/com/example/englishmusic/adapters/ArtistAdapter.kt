package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.model.ArtistItem
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class ArtistAdapter: RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {

   private val differCallback = object : DiffUtil.ItemCallback<ArtistItem>() {
        override fun areItemsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem._id == oldItem._id
        }

        override fun areContentsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem.hashCode() == oldItem.hashCode()
        }

    }

    inner class ArtistViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)


    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_artist,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
       holder.itemView.apply {
           val artist = differ.currentList[position]
           val artistImg = findViewById<ShapeableImageView>(R.id.artistImg)
           val artistText = findViewById<TextView>(R.id.artistName)
           Glide.with(this).load(artist.artistImg).into(artistImg)
           artistText.text = artist.name
           setOnClickListener {
               onItemClickListener?.let {
                   it(artist)
               }
           }

       }
    }

    private var onItemClickListener:((ArtistItem)->Unit)?=null
    fun setOnItemClickListener(listener:(ArtistItem)->Unit){
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
       return differ.currentList.size
    }


}