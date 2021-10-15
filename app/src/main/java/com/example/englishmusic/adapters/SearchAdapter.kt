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
import com.example.englishmusic.model.SearchItem
import com.google.android.material.imageview.ShapeableImageView

class SearchAdapter: RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {





    inner class SearchViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    val differCallback = object : DiffUtil.ItemCallback<SearchItem>(){
        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
           return  oldItem._id == newItem._id
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return  oldItem._id == newItem._id
        }

    }

    val differ = AsyncListDiffer(this,differCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
       return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_search,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val search = differ.currentList[position]
        holder.itemView.apply {
            val cover = findViewById<ShapeableImageView>(R.id.searchImg)
            val status = findViewById<TextView>(R.id.status)
            val songOrArtistTxt = findViewById<TextView>(R.id.songOrArtistTxt)
            status.text = search.status
            songOrArtistTxt.text = search.name
            Glide.with(this).load(search.imgUrl).into(cover)
            setOnClickListener {
                onClickListener?.let {
                    it(search)
                }
            }

        }
    }

   private var onClickListener : ((SearchItem) -> Unit)? = null
    fun setOnItemClickListener(listener:(SearchItem)-> Unit) {
        this.onClickListener = listener
    }



    override fun getItemCount(): Int {
      return  differ.currentList.size
    }

}