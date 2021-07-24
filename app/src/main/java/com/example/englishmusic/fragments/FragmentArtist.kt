package com.example.englishmusic.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.MusicInfoViewModel
import com.example.englishmusic.adapters.ArtistAdapter
import com.example.englishmusic.databinding.FragmentArtistBinding
import com.example.englishmusic.other.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentArtist:Fragment(R.layout.fragment_artist) {

    private var binding:FragmentArtistBinding? = null

    private lateinit var viewModel:MusicInfoViewModel


     lateinit var artistAdapter: ArtistAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArtistBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(
            MusicInfoViewModel::class.java
        )

        setUpRecyclerView()


        viewModel.getArtist()
        viewModel.Singer.observe(viewLifecycleOwner, Observer {  response ->

              when(response.status){

                  Status.SUCCESS ->{

                      response.data?.let {
                          artistAdapter.differ.submitList(it)
                      }
                  }

                  Status.ERROR ->{

                  }
              }



        })

        navigateToAlbum()






    }



    private fun setUpRecyclerView(){
        artistAdapter = ArtistAdapter()
        binding!!.artistRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding!!.artistRecyclerView.adapter = artistAdapter

    }

    private fun navigateToAlbum(){
        artistAdapter.setOnItemClickListener {
            val bundle = Bundle()
            bundle.putString("artist",it.name)
            findNavController().navigate(R.id.action_fragmentArtist_to_albumFragment,bundle)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}