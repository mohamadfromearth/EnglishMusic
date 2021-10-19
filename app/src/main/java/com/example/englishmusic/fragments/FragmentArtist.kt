package com.example.englishmusic.fragments


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.englishmusic.R
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.example.englishmusic.adapters.ArtistAdapter
import com.example.englishmusic.adapters.PlaylistAdapter
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.databinding.FragmentArtistBinding
import com.example.englishmusic.model.artist.Artist
import com.example.englishmusic.model.Playlist
import com.example.englishmusic.model.PlaylistItem
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.model.artist.ArtistItem
import com.example.englishmusic.model.song.SerialRecentlySong
import com.example.englishmusic.model.song.SongItem
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.log

@AndroidEntryPoint
class FragmentArtist:Fragment(R.layout.fragment_artist) {
    private var binding:FragmentArtistBinding? = null
    private lateinit var viewModel: MusicInfoViewModel
    private lateinit var mainViewModel : MainViewModel
    private var token = ""
    @Inject
    lateinit var sharePref:SharedPreferences
    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var playListAdapter: PlaylistAdapter
    private lateinit var recentlyPlayedAdapter: SongAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentArtistBinding.bind(view)
        binding = FragmentArtistBinding.bind(view)
        initial()
        //getArtistAndPlayList()
        setUpRecyclerViews()
        subscribeToObserver()
        artistAdapter.setOnItemClickListener {
           navigateToAlbumFragment(it)

        }
        playListAdapter.setOnItemClickListener {
            navigateToPlayListFragment(it)

        }

        binding!!.tryAgainBtn.setOnClickListener {
            getArtistAndPlayList()
        }
    }
    private fun subscribeToObserver(){
        viewModel.shouldUpdateMyArtist.observe(viewLifecycleOwner,  {
            if (it) viewModel.getMyArtists(token)
        })

        viewModel.playlist.observe(viewLifecycleOwner,  { result ->
            when(result.status){
                Status.LOADING -> {
                    hideTryAgainNetworkFailurePlayListTxts()
                    showLoadingAnimation()

                }

                Status.SUCCESS -> {

                    hideLoadingAnimation()
                    showPlaylistTxtAndOther()
                    result.data?.let {

                        playListAdapter.differ.submitList(it)
                    }
                }

                Status.ERROR -> {
                    submitEmptyListAndShowTryAgainBtn()
                }

                else -> Unit
            }

        })
        viewModel.myArtists.observe(viewLifecycleOwner,  {  result ->
            when(result.status){
                Status.LOADING -> {
                   loading()
                }
                Status.SUCCESS -> {
                    hideLoadingAnimation()
                    result.data?.let { artist->



                        if (artist.isEmpty()){
                         artistAdapter.differ.submitList(Artist())
                        }else{
                            showArtistTxt()
                            if (artist.size<10) binding!!.myArtistMore.visibility = View.GONE
                            artistAdapter.differ.submitList(artist)
                        }

                    }
                }
                Status.ERROR -> {

                }
                else -> Unit


            }

        })

        viewModel.getRecentlySong().observe(viewLifecycleOwner, { song ->
            val songs = ArrayList<SongItem>()

            recentlyPlayedAdapter.setOnItemClick {
                val bundle = Bundle().apply {
                    putSerializable("song",SerialRecentlySong(song))
                }

                mainViewModel.addCustomAction(bundle,"recentlySong")
                mainViewModel.playOrToggleSong(it)
                findNavController().navigate(R.id.action_fragmentArtist_to_songPlayingFragment)
            }
          recentlyPlayedAdapter.differ.submitList(song)


        })

    }



    private fun setUpRecyclerViews(){
        artistAdapter = ArtistAdapter(R.layout.item_big_circle_artist)
        playListAdapter = PlaylistAdapter()
        recentlyPlayedAdapter = SongAdapter(R.layout.item_top_song,0)
        binding!!.artistRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
        binding!!.artistRecyclerView.adapter = artistAdapter
        binding!!.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
        binding!!.playlistRecyclerView.adapter = playListAdapter
        binding!!.recentlyPlayedRecyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding!!.recentlyPlayedRecyclerView.adapter = recentlyPlayedAdapter
    }

    private fun loading(){
        binding!!.myArtistTxt.visibility = View.GONE
        binding!!.myArtistMore.visibility = View.GONE
    }


    private fun showArtistTxt(){
        binding!!.myArtistTxt.visibility = View.VISIBLE
        binding!!.myArtistMore.visibility = View.VISIBLE
        binding!!.myArtistTxt.visibility = View.VISIBLE
    }


    private fun submitEmptyListAndShowTryAgainBtn(){
        playListAdapter.differ.submitList(Playlist())
        artistAdapter.differ.submitList(Artist())
        binding!!.loading.visibility = View.GONE
        binding!!.playlistTxt.visibility = View.GONE
        binding!!.myArtistTxt.visibility = View.GONE
        binding!!.myArtistMore.visibility = View.GONE
        binding!!.networkFailureTxt.visibility = View.VISIBLE
        binding!!.tryAgainBtn.visibility = View.VISIBLE
    }

  private fun initial(){
      token = sharePref.getString("token","token").toString()
      viewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
      mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
  }


    private fun getArtistAndPlayList(){
        viewModel.getMyArtists(token)
          viewModel.getPlayLists()
    }


    private fun navigateToAlbumFragment(it:ArtistItem){
        val bundle = Bundle()
        bundle.putString("artist",it.name)
        bundle.putString("imageUrl",it.artistImg)
        bundle.putString("id",it._id)
        findNavController().navigate(R.id.action_fragmentArtist_to_albumFragment,bundle)
    }


    private fun navigateToPlayListFragment(it:PlaylistItem){
        val playlist = it.name
        val bundle=Bundle()
        bundle.putString("playlist",playlist)
        findNavController().navigate(R.id.action_fragmentArtist_to_playlistSongFragment,bundle)
    }


    private fun hideTryAgainNetworkFailurePlayListTxts(){
        binding!!.tryAgainBtn.visibility = View.GONE
        binding!!.networkFailureTxt.visibility = View.GONE
        binding!!.playlistTxt.visibility = View.GONE
        binding!!.recentlyPlayedRecyclerView.visibility = View.GONE
        binding!!.recentlyPlayedTxt.visibility = View.GONE
    }

    private fun showLoadingAnimation(){
        binding!!.loading.visibility = View.VISIBLE
    }


    private fun hideLoadingAnimation(){
        binding!!.loading.visibility = View.GONE
    }

    private fun showPlaylistTxtAndOther() {
        binding!!.playlistTxt.visibility = View.VISIBLE
        binding!!.recentlyPlayedRecyclerView.visibility = View.VISIBLE
        binding!!.recentlyPlayedTxt.visibility = View.VISIBLE
    }

}



