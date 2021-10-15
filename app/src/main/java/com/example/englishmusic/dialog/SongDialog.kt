package com.example.englishmusic.dialog

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.englishmusic.R
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.BottomSheetDialogFragmentBinding
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.model.SongItemFav
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class SongDialog(private val songItem:SongItem):BottomSheetDialogFragment(){
    private lateinit var binding:BottomSheetDialogFragmentBinding

    @Inject
    lateinit var sharePref:SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_dialog_fragment,container,false)
        binding = BottomSheetDialogFragmentBinding.bind(view)
        setUpListView()
        return view

    }

    private fun setUpListView(){
        val songOptions = listOf<String>("Add to favorites","Add to playlist","Share")
        binding.bottomSheetListView.adapter = ArrayAdapter<String>(requireContext(),R.layout.simle_list,songOptions)
        binding.bottomSheetListView.setOnItemClickListener{parent,view,position,id ->
            when(position){
                0 ->{
                 addToFavorites()
                }
                1 -> {

                }
                2 -> {
                shareSong()
                }
            }
        }
    }

    private fun addToFavorites(){
        val token = sharePref.getString("token","token").toString()
        CoroutineScope(Dispatchers.Main).launch {
            RetrofitInstance.api.addFavorites(token, SongItemFav(0,songItem._id,songItem.album
            ,songItem.artist,songItem.cover,songItem.duration,songItem.name
            ,songItem.songUrl))

        }
        dismiss()
    }

    private fun shareSong(){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/*"
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Space Music")
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, songItem.songUrl)
        startActivity(Intent.createChooser(sharingIntent,"Share via"))

    }

}