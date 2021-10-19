package com.example.englishmusic.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.model.artist.ArtistItem
import de.hdodenhof.circleimageview.CircleImageView


class SelectArtistsAdapter:RecyclerView.Adapter<SelectArtistsAdapter.ArtistViewHolder>() {
    var isSelected = false

    private val differCallback = object : DiffUtil.ItemCallback<ArtistItem>() {
        override fun areItemsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem._id == oldItem._id
        }

        override fun areContentsTheSame(oldItem: ArtistItem, newItem: ArtistItem): Boolean {
            return oldItem.hashCode() == oldItem.hashCode()
        }

    }

    inner class ArtistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)


    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        return ArtistViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_circle_artist,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.itemView.apply {
            val artist = differ.currentList[position]
            val artistImg = findViewById<CircleImageView>(R.id.artistImg)
            val artistText = findViewById<TextView>(R.id.artistName)
            Glide.with(this).load(artist.artistImg).into(artistImg)

            artistImg.borderWidth = 4
            artistText.text = artist.name
            setOnClickListener {
                if (artistImg.borderColor == Color.BLACK) {
                    artistImg.borderColor = Color.GREEN
                    isSelected = false
                }
                else{
                    artistImg.borderColor = Color.BLACK
                    isSelected = true
                }


                onItemClickListener?.let {
                    it(artist,isSelected)
                }
            }

        }
    }

    private var onItemClickListener:((ArtistItem, isSelected:Boolean)->Unit)?=null
    fun setOnItemClickListener(listener:(ArtistItem, isSelected:Boolean)->Unit){
        onItemClickListener = listener
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}