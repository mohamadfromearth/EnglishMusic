package com.example.englishmusic.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.databinding.FragmentSongBinding
import com.example.englishmusic.exoPlayer.MusicService
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject




@AndroidEntryPoint
class SongFragment: Fragment(R.layout.fragment_song) {

    private  var binding: FragmentSongBinding? = null

    private lateinit var mainViewModel: MainViewModel



    @Inject
    lateinit var songAdapter: SongAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongBinding.bind(view)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)



        setUpRecyclerView()
        subscribeToObservers()

         binding!!.test2.setOnClickListener {
             mainViewModel.album = ""
             mainViewModel.init()
         }


       songAdapter.setOnItemClick {

           mainViewModel.playOrToggleSong(it)

       }




    }


    private fun setUpRecyclerView(){
        binding!!.songRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.songRecyclerView.adapter = songAdapter
    }

    private fun subscribeToObservers(){



        mainViewModel.init()


        mainViewModel.curPlayingSong.observe(viewLifecycleOwner, Observer {
             it?.let {

             }
        })


        mainViewModel.playbackState.observe(viewLifecycleOwner, Observer {

        })



        mainViewModel.mediaItem.observe(viewLifecycleOwner, Observer { result ->

            when(result.status){
                Status.SUCCESS ->{
                    result.data?.let {
                        songAdapter.differ.submitList(it)
                    }
                }
            }

        })


        mainViewModel.isConnected.observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(),it.hasBeenHandled.toString(),Toast.LENGTH_SHORT).show()

        })


    }



}