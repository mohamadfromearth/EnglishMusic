package com.example.englishmusic.fragments

import android.content.Intent
import android.net.Uri
import android.nfc.Tag
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.englishmusic.IDeleteDownload
import com.example.englishmusic.R
import com.example.englishmusic.databinding.BottomSheetDialogFragmentBinding
import com.example.englishmusic.dialog.DeleteDownloadDialog
import com.example.englishmusic.model.DownloadSong
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class DownloadBottomSheetDialogFragment(private val downloadSong:DownloadSong,
private val callback:IDeleteDownload) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFragmentBinding

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
        val optionList = listOf<String>("Delete","Share")
        val bottomSheetAdapter = ArrayAdapter<String>(requireContext(),R.layout.simle_list,optionList)
        binding.bottomSheetListView.adapter = bottomSheetAdapter
        binding.bottomSheetListView.setOnItemClickListener { parent, view, position, id ->
          when(position){
              0 -> {

                 DeleteDownloadDialog(){
                  dismiss()
                 }.show(childFragmentManager,tag)


              }
              1 ->{

                  val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                  sharingIntent.type = "audio/*"
                  //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Space Music")
                  sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(File(tag)))
                  startActivity(Intent.createChooser(sharingIntent,"Share via"))

              }
          }
        }
    }




}