package com.example.englishmusic.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.MusicInfoViewModel
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.databinding.FragmentAlbumBinding
import com.example.englishmusic.other.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AlbumFragment:Fragment(R.layout.fragment_album) {

    var binding:FragmentAlbumBinding? = null

    private lateinit var viewModel: MusicInfoViewModel

    @Inject
    lateinit var albumAdapter:AlbumAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAlbumBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(
            MusicInfoViewModel::class.java
        )

        setUpRecyclerView()


        viewModel.getAlbum(arguments?.getString("artist").toString())
        viewModel.albums.observe(viewLifecycleOwner, Observer { response ->
            when(response.status){

                Status.SUCCESS ->{
                    response.data?.let {
                        albumAdapter.differ.submitList(it)
                    }
                }

            }
        })

        albumAdapter.setItemClickListener {

            val bundle = Bundle()
            bundle.putString("album",it.name)

            findNavController().navigate(R.id.action_albumFragment_to_songFragment,bundle)
        }





    }



    private fun setUpRecyclerView(){
        binding!!.albumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.albumRecyclerView.adapter = albumAdapter
    }


}