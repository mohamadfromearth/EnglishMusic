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
import com.example.englishmusic.databinding.FragmentMySongBinding
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.model.Song
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MySongFragment: Fragment(R.layout.fragment_my_song) {

    private  var binding: FragmentMySongBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var songAdapter: SongAdapter
    @Inject
    lateinit var sharePref:SharedPreferences
    private var token = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMySongBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        val bundle = Bundle()
        setUpRecyclerView()
        getFavorites()
        musicInfoViewModel.favorites.observe(viewLifecycleOwner, Observer {  result ->
            when(result.status){
                Status.LOADING -> {
                    binding!!.networkFailureTxt.visibility = View.GONE
                    binding!!.tryAgainBtn.visibility = View.GONE
                    binding!!.loading.visibility = View.VISIBLE
                }

                Status.SUCCESS ->{
                    binding!!.loading.visibility = View.GONE
                    result.data?.let { song ->
                        if (song.isEmpty()){
                            binding!!.emptyList.visibility = View.VISIBLE
                        }else{
                            songAdapter.differ.submitList(song)
                        }


                        songAdapter.setOnItemClick {
                            bundle.putSerializable("song", SerializableSong(song))
                            mainViewModel.addCustomAction(bundle,"song")
                            mainViewModel.playOrToggleSong(it)
                            findNavController().navigate(R.id.action_mySongFragment_to_songPlayingFragment)
                        }






                    }
                }

                Status.ERROR->{
                    songAdapter.differ.submitList(Song())
                    binding!!.loading.visibility = View.GONE
                    binding!!.networkFailureTxt.visibility = View.VISIBLE
                    binding!!.tryAgainBtn.visibility = View.VISIBLE
                }
            }

        })
        binding!!.tryAgainBtn.setOnClickListener {
            getFavorites()
        }

    }
    private fun setUpRecyclerView(){
        songAdapter = SongAdapter(R.layout.item_songs,0)
        binding!!.songRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.songRecyclerView.adapter = songAdapter
    }
    private fun getFavorites(){
        token = sharePref.getString("token",token).toString()
        musicInfoViewModel.getFavorites(token)
    }
    override fun onDestroy() {
        musicInfoViewModel.songs.postValue(Resource.clean())

        super.onDestroy()
    }



}
