package com.example.englishmusic.fragments.downloads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.englishmusic.R
import com.example.englishmusic.databinding.BottomSheetDialogFragmentBinding
import com.example.englishmusic.dialog.DeleteDownloadDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File

class DownloadBottomSheetDialogFragment : BottomSheetDialogFragment() {

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

        val optionList = listOf("Delete","Share")
        val bottomSheetAdapter = ArrayAdapter(requireContext(),R.layout.simle_list,optionList)
        binding.bottomSheetListView.adapter = bottomSheetAdapter
        binding.bottomSheetListView.setOnItemClickListener { _, _, position, _ ->
          when(position){
              0 -> {


               showDeleteDownloadDialog()

              }
              1 ->{

                showShareToOtherApp()


              }
          }
        }
    }


   private fun showDeleteDownloadDialog(){
       DeleteDownloadDialog{
           dismiss()
       }.show(childFragmentManager,tag)
   }


   private fun showShareToOtherApp(){
       val sharingIntent = Intent(Intent.ACTION_SEND)
       sharingIntent.type = "audio/*"
       //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Space Music")
       tag?.let {
           sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(it)))
           startActivity(Intent.createChooser(sharingIntent,"Share via"))
       }
   }

}