package com.example.englishmusic.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.IDeleteDownload
import com.example.englishmusic.R
import com.example.englishmusic.adapters.DownloadAdapter
import com.example.englishmusic.databinding.FragmentDownloadBinding
import com.example.englishmusic.model.*
import com.example.englishmusic.viewmodel.DownloadViewModel
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import kotlin.math.log

class DownloadFragment:Fragment(R.layout.fragment_download) ,IDeleteDownload {

    private lateinit var mainViewModel: MainViewModel

    private lateinit var downloadViewModel: DownloadViewModel
    private var binding:FragmentDownloadBinding?=null

    private lateinit var downloadAdapter:DownloadAdapter

    private lateinit var song : ArrayList<DownloadSong>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDownloadBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        downloadViewModel = ViewModelProvider(requireActivity()).get(DownloadViewModel::class.java)
        setUpRecyclerView()
        downloadViewModel.downloadedSong.observe(viewLifecycleOwner, Observer {
            song = it as ArrayList<DownloadSong>
            downloadAdapter.differ.submitList(it)
        })
        downloadAdapter.setOnClickListener {
            val bundle =Bundle()
            bundle.putSerializable("song",SerializableDownloadSong(song))
            mainViewModel.addCustomAction(bundle,"songDownload")
            mainViewModel.playOrToggleSong(SongItem(0,it.id,"downloading","","",0,
            it.songName,it.songPath,""))
            mainViewModel.songBitmap.postValue(it.songImg)

            findNavController().navigate(R.id.action_downloadFragment_to_songPlayingFragmentForDownloads)
        }
        downloadAdapter.setOnOptionClickListener {
            DownloadBottomSheetDialogFragment(it,this).show(childFragmentManager,it.songPath)
        }





    }

    private fun setUpRecyclerView(){
        downloadAdapter = DownloadAdapter()
        binding!!.downloadRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.downloadRecyclerView.adapter = downloadAdapter
    }
  /*  private fun getDownloads() {

        val file = requireActivity().getExternalFilesDir(Environment.getDataDirectory().absolutePath)
                ?.listFiles()
        val mmr = MediaMetadataRetriever()
        var inputStream: InputStream? = null
        file?.filter { it.canRead() && it.isFile && it.name.endsWith(".mp3") }?.map {
            mmr.setDataSource(it.absolutePath)
            inputStream = ByteArrayInputStream(mmr.embeddedPicture)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            song.add(DownloadSong(it.hashCode().toString(),
                it.name,
                it.absolutePath,
                bitmap))
        }
*/

  //  }



    override fun deleteDownload(downloadSong: DownloadSong) {
       downloadViewModel.getDownloadSong()

    }


}
