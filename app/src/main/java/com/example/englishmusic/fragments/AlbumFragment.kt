package com.example.englishmusic.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.MainActivity
import com.example.englishmusic.R
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.databinding.FragmentAlbumBinding
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AlbumFragment:Fragment(R.layout.fragment_album) {

    var binding:FragmentAlbumBinding? = null

    private lateinit var viewModel: MusicInfoViewModel


    lateinit var albumAdapter:AlbumAdapter

    private lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAlbumBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(
            MusicInfoViewModel::class.java
        )
        mainViewModel = (activity as MainActivity).mainViewModel

        setUpRecyclerView()




        viewModel.getAlbum(arguments?.getString("artist").toString())
        viewModel.albums.observe(viewLifecycleOwner, Observer { response ->
            when(response.status){

                Status.LOADING ->{

                }

               Status.SUCCESS ->{

                    response.data?.let {
                        albumAdapter.differ.submitList(it)
                    }
                }

            }
        })

        albumAdapter.setItemClickListener {
            mainViewModel.unsubscribe(it.name)
            val bundle = Bundle()
            bundle.putString("album",it.name)

            findNavController().navigate(R.id.action_albumFragment_to_songFragment,bundle)
        }





    }





    override fun onDestroy() {
        viewModel.albums.postValue(Resource.clean())
        super.onDestroy()
    }



    private fun setUpRecyclerView(){
        albumAdapter = AlbumAdapter()
        binding!!.albumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.albumRecyclerView.adapter = albumAdapter
    }


}