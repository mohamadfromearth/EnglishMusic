package com.example.englishmusic.fragments.songs

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.databinding.FragmentAllTopSongBinding
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.model.song.Song
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel

class AllTopSongsFragment:Fragment(R.layout.fragment_all_top_song) {
    private  var binding:FragmentAllTopSongBinding?=null
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var topSongAdapter:SongAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllTopSongBinding.bind(view)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRecyclerView()
        subscribeToObservers()

    }
    private fun setUpRecyclerView(){
        topSongAdapter = SongAdapter(R.layout.item_songs,0)
        binding!!.allTopSongRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.allTopSongRecyclerView.adapter = topSongAdapter
    }
    private fun subscribeToObservers(){
        musicInfoViewModel.topSongs.observe(viewLifecycleOwner,{ result ->
            when(result.status){
                Status.LOADING -> {

                }
                Status.SUCCESS->{
                  submitSongListAndSetOnClickForTopSongs(result)
                }

              Status.ERROR -> {

              }
             else -> Unit
            }

        })
    }

    private fun submitSongListAndSetOnClickForTopSongs(result: Resource<Song>){
        result.data?.let {  song->
            topSongAdapter.differ.submitList(song)
            topSongAdapter.setOnItemClick {
                val bundle = Bundle()
                bundle.putSerializable("song", SerializableSong(song))
                mainViewModel.addCustomAction(bundle,"song")
                mainViewModel.playOrToggleSong(it)
                findNavController().navigate(R.id.action_allTopSongsFragment_to_songPlayingFragment)
            }
        }
    }

}