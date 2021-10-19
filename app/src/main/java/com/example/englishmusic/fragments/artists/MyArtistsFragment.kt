package com.example.englishmusic.fragments.artists

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.ArtistAdapter
import com.example.englishmusic.databinding.FragmentMyArtistBinding
import com.example.englishmusic.model.artist.Artist
import com.example.englishmusic.model.artist.ArtistItem
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class MyArtistsFragment: Fragment(R.layout.fragment_my_artist) {
    private var binding:FragmentMyArtistBinding? = null
    private lateinit var viewModel:MusicInfoViewModel
    private lateinit var artistAdapter:ArtistAdapter
    @Inject
    lateinit var sharePref:SharedPreferences
    private var token = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyArtistBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        getMyArtists()
        setUpRecyclerView()
        subscribeToObservers()
        artistAdapter.setOnItemClickListener {
            navigateToAlbumFragment(it)

        }
        binding!!.tryAgainBtn.setOnClickListener {
            getMyArtists()
        }
    }
    private fun setUpRecyclerView(){
        artistAdapter = ArtistAdapter(R.layout.my_artist_item)
        binding!!.myArtistRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding!!.myArtistRecyclerView.adapter = artistAdapter
    }
    private fun getMyArtists(){
        token = sharePref.getString("token","token").toString()
        viewModel.getMyArtists(token)
    }
    private fun subscribeToObservers(){
        viewModel.myArtists.observe(viewLifecycleOwner,  { result ->
            when(result.status){
                Status.LOADING->{
                   showLoadingProgressBarAndHideTxt()

                }
                Status.SUCCESS->{
                   submitListToDiffer(result)
                }
                Status.ERROR->{
                   submitEmptyListToDifferAndShowTryAgainBtn()

                }
              else -> Unit
            }

        })
    }

   private fun navigateToAlbumFragment(it:ArtistItem){
       val bundle = Bundle()
       bundle.putString("artist",it.name)
       bundle.putString("imageUrl",it.artistImg)
       bundle.putString("id",it._id)
       findNavController().navigate(R.id.action_myArtistsFragment_to_albumFragment,bundle)
   }


    private fun showLoadingProgressBarAndHideTxt(){
        binding!!.loading.visibility = View.VISIBLE
        binding!!.networkFailureTxt.visibility = View.GONE
        binding!!.tryAgainBtn.visibility = View.GONE
    }


    private fun submitListToDiffer(result:Resource<Artist>){
        binding!!.loading.visibility = View.GONE
        result.data?.let {
            if (it.isEmpty()){
                binding!!.emptyList.visibility = View.VISIBLE
            }else{
                artistAdapter.differ.submitList(it)
            }

        }
    }



    private fun submitEmptyListToDifferAndShowTryAgainBtn(){
        artistAdapter.differ.submitList(Artist())
        binding!!.loading.visibility = View.GONE
        binding!!.networkFailureTxt.visibility = View.VISIBLE
        binding!!.tryAgainBtn.visibility = View.VISIBLE
    }


}