package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.englishmusic.R
import com.example.englishmusic.model.ProfileList

class ProfileAdapter(val data:ArrayList<ProfileList>):RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {


    inner class ProfileViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        return ProfileViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_artist,parent,false
            ))}
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profItem = data[position]
        holder.itemView.apply {
            val nameTxt = findViewById<TextView>(R.id.artistName)
            val profileListImg = findViewById<ImageView>(R.id.artistImg)
            nameTxt.text = profItem.name
            profileListImg.setImageResource(profItem.img)
            setOnClickListener {
              onClick?.let {
                  it(profItem.name)
              }
            }
        }
    }

    private var onClick:((name:String)->Unit)?=null
    fun setOnClickListener(listener:(name:String)->Unit){
        onClick = listener
    }

    override fun getItemCount(): Int {
        return data.size
    }
}