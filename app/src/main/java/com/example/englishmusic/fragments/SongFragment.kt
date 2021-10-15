package com.example.englishmusic.fragments
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.api.AlbumId
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.FragmentSongBinding
import com.example.englishmusic.dialog.SongDialog
import com.example.englishmusic.exoPlayer.MusicService
import com.example.englishmusic.model.*
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
@AndroidEntryPoint
class SongFragment: Fragment(R.layout.fragment_song) {
    private  var binding: FragmentSongBinding? = null
    private lateinit var mainViewModel: MainViewModel
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private lateinit var songAdapter: SongAdapter
    @Inject
    lateinit var sharePref:SharedPreferences
    private var token = ""
    private var albumId = ""
    private val bundle = Bundle()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        assignValues()
        musicInfoViewModel.checkIsAlbumFavorite(token, FavoriteId(albumId))
        musicInfoViewModel.getSong(arguments?.getString("album").toString())
        setAlbumImage()
        setUpRecyclerView()
        subscribeToObservers()
        binding!!.addDeleteAlbum.setOnClickListener {
            if (binding!!.addDeleteAlbum.getTag(FILL_HEART) == EMPTY_HEART  ){
                addAlbum()
                binding!!.addDeleteAlbum.setImageResource(FILL_HEART)
                binding!!.addDeleteAlbum.setTag(FILL_HEART, FILL_HEART)
            }else{
                deleteAlbum()
                binding!!.addDeleteAlbum.setImageResource(EMPTY_HEART)
                binding!!.addDeleteAlbum.setTag(FILL_HEART, EMPTY_HEART)
            }
        }
        songAdapter.setOnOptionClick {
            SongDialog(it).show(childFragmentManager,it._id)
        }


    }
    private fun setUpRecyclerView(){
        songAdapter = SongAdapter(R.layout.item_songs,0)
        binding!!.songRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.songRecyclerView.adapter = songAdapter
    }
    private fun subscribeToObservers(){
        musicInfoViewModel.songs.observe(viewLifecycleOwner, Observer {  result ->
            when(result.status){
                Status.LOADING -> {

                }

                Status.SUCCESS ->{
                    result.data?.let { song ->
                        songAdapter.differ.submitList(song)

                        songAdapter.setOnItemClick {
                            bundle.putSerializable("song",SerializableSong(song))
                            mainViewModel.addCustomAction(bundle,"song")
                            mainViewModel.playOrToggleSong(it)
                            val bundle = Bundle()
                            bundle.putString("songUri",it.songUrl)
                            findNavController().navigate(R.id.action_songFragment_to_songPlayingFragment,bundle)
                        }






                    }
                }
            }

        })
        musicInfoViewModel.isAlbumFavorite.observe(viewLifecycleOwner, Observer { result ->
            when(result.status){
                Status.LOADING->{

                }
                Status.SUCCESS->{
                    result.data?.let {

                        if (it.isFavorite){
                            binding!!.addDeleteAlbum.setImageResource(FILL_HEART)
                            binding!!.addDeleteAlbum.setTag(FILL_HEART, FILL_HEART)
                        }else{
                            binding!!.addDeleteAlbum.setImageResource(EMPTY_HEART)
                            binding!!.addDeleteAlbum.setTag(FILL_HEART, EMPTY_HEART)
                        }
                    }
                }
            }

        })
    }
    private fun setAlbumImage(){
        val albumImg = arguments?.getString("albumImg")
        Glide.with(requireContext()).load(albumImg).into(binding!!.albumImg)
        binding!!.albumName.text = arguments?.getString("artistName").toString()

    }
    private fun addAlbum(){
        val artistName = arguments?.getString("artistName").toString()
        val albumName =  arguments?.getString("album").toString()
        val albumImg = arguments?.getString("albumImg").toString()
        val released = arguments?.getString("released").toString()
        CoroutineScope(Dispatchers.Main).launch {
            try{
                RetrofitInstance.api.addAlbum(token, AddAlbum(albumId,artistName,albumImg,albumName,released))
            }catch (t:Throwable){

            }

        }

    }
    private fun deleteAlbum(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitInstance.api.deleteAlbum(token, AlbumId(albumId))
            }catch (t:Throwable){

            }
        }
    }
    private fun assignValues(){
        token = sharePref.getString("token","token").toString()
        albumId = arguments?.getString("albumId").toString()
    }
    override fun onDestroy() {
         musicInfoViewModel.songs.postValue(Resource.clean())

         super.onDestroy()
    }
}