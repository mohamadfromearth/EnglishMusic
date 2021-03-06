package com.example.englishmusic.fragments.albums

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.databinding.MyAlbumsFragmentBinding
import com.example.englishmusic.model.albums.Album
import com.example.englishmusic.model.albums.AlbumItem
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyAlbumsFragment: Fragment(R.layout.my_albums_fragment) {

    private var binding:MyAlbumsFragmentBinding? = null
    @Inject
    lateinit var sharePref:SharedPreferences

    private lateinit var albumAdapter:AlbumAdapter

    private lateinit var viewModel:MusicInfoViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MyAlbumsFragmentBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        getMyAlbums()
        setUpRecyclerView()
        subscribeToObservers()
        albumAdapter.setItemClickListener {
           navigateToSongFragment(it)
        }
        binding!!.tryAgainBtn.setOnClickListener {
            getMyAlbums()
        }
    }

    private fun setUpRecyclerView(){
        albumAdapter = AlbumAdapter(R.layout.item_album_first,0)
        binding!!.myAlbumsRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding!!.myAlbumsRecyclerView.adapter = albumAdapter
    }
    private fun getMyAlbums(){
      val token= sharePref.getString("token","token").toString()
        viewModel.getMyAlbum(token)
    }
    private fun subscribeToObservers(){
        viewModel.myAlbums.observe(viewLifecycleOwner, { result ->
        when(result.status){
            Status.LOADING-> {
               loading()

            }
            Status.SUCCESS->{

                submitListToDiffer(result)
            }
            Status.ERROR->{
               hide()
            }

            else -> Unit
        }

        })
    }


    private fun navigateToSongFragment(it:AlbumItem){
        val bundle = Bundle()
        bundle.putString("album",it.name)
        bundle.putString("albumImg",it.imageUrl)
        bundle.putString("artistName",it.artist)
        bundle.putString("albumId",it._id)
        bundle.putString("released",it.released)
        findNavController().navigate(R.id.action_myAlbumsFragment_to_songFragment,bundle)
    }


    private fun loading(){
        binding!!.networkFailureTxt.visibility = View.GONE
        binding!!.tryAgainBtn.visibility = View.GONE
        binding!!.loading.visibility = View.VISIBLE
    }


    private fun submitListToDiffer(result:Resource<Album>){
        binding!!.loading.visibility = View.GONE
        result.data?.let {
            if (it.isEmpty()){
                albumAdapter.differ.submitList(Album())
                binding!!.emptyList.visibility = View.VISIBLE
            }


            albumAdapter.differ.submitList(it)


        }
    }


    private fun hide(){
        binding!!.loading.visibility = View.GONE
        binding!!.networkFailureTxt.visibility = View.VISIBLE
        binding!!.tryAgainBtn.visibility = View.VISIBLE
    }

}