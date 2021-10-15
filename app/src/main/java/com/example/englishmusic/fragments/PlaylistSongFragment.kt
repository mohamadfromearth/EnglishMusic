package com.example.englishmusic.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.databinding.FragmentPlaylistsSongBinding
import com.example.englishmusic.databinding.FragmentSongBinding
import com.example.englishmusic.databinding.FragmentSongPlayBinding
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PlaylistSongFragment:Fragment(R.layout.fragment_playlists_song) {

    @Inject
    lateinit var sharePref:SharedPreferences

    private lateinit var mainViewModel:MainViewModel

    private lateinit var musicViewModel:MusicInfoViewModel

    private lateinit var songAdapter: SongAdapter

    private var binding:FragmentPlaylistsSongBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val token = sharePref.getString("token","token").toString()
        binding = FragmentPlaylistsSongBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        musicViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        musicViewModel.getPlaylistSong(token,arguments?.getString("playlist").toString())
        setUpRecyclerView()
        musicViewModel.playlistsSong.observe(viewLifecycleOwner, Observer { result ->
            val bundle = Bundle()
            when(result.status){
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    result.data?.let { song ->
                        songAdapter.differ.submitList(song)
                        songAdapter.setOnItemClick {
                            bundle.putSerializable("song", SerializableSong(song))
                            mainViewModel.addCustomAction(bundle,"song")
                            mainViewModel.playOrToggleSong(it)
                            findNavController().navigate(R.id.action_playlistSongFragment_to_songPlayingFragment)
                        }

                    }
                }

                Status.ERROR -> {

                }

            }

        })




    }


    private fun setUpRecyclerView(){
        songAdapter = SongAdapter(R.layout.item_songs,0)
        binding!!.songRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.songRecyclerView.adapter = songAdapter
    }

    private fun subscribeToObservers(){

    }

    override fun onDestroy() {
        super.onDestroy()
        musicViewModel.playlistsSong.postValue(Resource.clean())
        binding = null
    }


}