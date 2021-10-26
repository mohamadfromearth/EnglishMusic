package com.example.englishmusic.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.englishmusic.R
import com.example.englishmusic.model.lyric.SubtitleOption

class SubtitleOptionAdapter( private val data:ArrayList<SubtitleOption>): RecyclerView.Adapter<SubtitleOptionAdapter.SubtitleOptionViewHolder>() {



    inner class SubtitleOptionViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtitleOptionViewHolder {
        return SubtitleOptionViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_subtitle_options,parent,false
            )
        )
    }

    override fun onBindViewHolder(holder: SubtitleOptionViewHolder, position: Int) {
        val optionItem = data[position]
        holder.itemView.apply {
            val checkBox = findViewById<CheckBox>(R.id.checkBox)
            checkBox.isChecked = optionItem.isChecked
            checkBox.text = optionItem.name
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                onCheckBoxClick?.let {
                    it(optionItem,isChecked)
                }

            }
        }
    }

    private var onCheckBoxClick:((SubtitleOption,isChecked:Boolean)->Unit)? = null
    fun setOnCheckBoxClickListener(listener:(SubtitleOption,isChecked:Boolean)->Unit){
        onCheckBoxClick = listener
    }


    override fun getItemCount(): Int {
        return data.size
    }


}