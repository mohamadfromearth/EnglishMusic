package com.example.englishmusic.fragments.downloads

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.IDeleteDownload
import com.example.englishmusic.R
import com.example.englishmusic.adapters.DownloadAdapter
import com.example.englishmusic.databinding.FragmentDownloadBinding
import com.example.englishmusic.model.*
import com.example.englishmusic.model.downloads.DownloadSong
import com.example.englishmusic.model.song.SongItem
import com.example.englishmusic.viewmodel.DownloadViewModel
import com.example.englishmusic.viewmodel.MainViewModel

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
        downloadViewModel.downloadedSong.observe(viewLifecycleOwner,  {
            song = it as ArrayList<DownloadSong>
            downloadAdapter.differ.submitList(it)
        })
        downloadAdapter.setOnClickListener {
           playSongAndNavigateToSongPlayingFragment(it)
        }
        downloadAdapter.setOnOptionClickListener {
            DownloadBottomSheetDialogFragment().show(childFragmentManager,it.songPath)
        }





    }

    private fun setUpRecyclerView(){
        downloadAdapter = DownloadAdapter()
        binding!!.downloadRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.downloadRecyclerView.adapter = downloadAdapter
    }




    override fun deleteDownload(downloadSong: DownloadSong) {
       downloadViewModel.getDownloadSong()

    }


    private fun playSongAndNavigateToSongPlayingFragment(it:DownloadSong){
        val bundle =Bundle()
        bundle.putSerializable("song",SerializableDownloadSong(song))
        mainViewModel.addCustomAction(bundle,"songDownload")
        mainViewModel.playOrToggleSong(
            SongItem(0,it.id,"downloading","","",0,
                it.songName,it.songPath,"")
        )
        mainViewModel.songBitmap.postValue(it.songImg)

        findNavController().navigate(R.id.action_downloadFragment_to_songPlayingFragmentForDownloads)
    }



}
