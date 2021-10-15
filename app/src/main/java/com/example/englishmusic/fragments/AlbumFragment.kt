package com.example.englishmusic.fragments
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.englishmusic.MainActivity
import com.example.englishmusic.R
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.api.FollowedId
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.BottomSheetArtistInfoBinding
import com.example.englishmusic.databinding.FragmentAlbumBinding
import com.example.englishmusic.model.AddArtist
import com.example.englishmusic.model.ArtistId
import com.example.englishmusic.model.SerializableSong
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class AlbumFragment:Fragment(R.layout.fragment_album) {
    private var binding:FragmentAlbumBinding? = null
    private lateinit var viewModel: MusicInfoViewModel
    private lateinit var albumAdapter:AlbumAdapter
    private lateinit var topSongAdapter: SongAdapter
    @Inject
    lateinit var sharePre:SharedPreferences
    private var token = ""
    private var followedId = ""
    private lateinit var mainViewModel: MainViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAlbumBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        viewModel.shouldUpdateMyArtist(false)
        //viewModel.myArtists.postValue(Resource.clean())
        subscribeToObservers()
        val artistName = arguments?.getString("artist").toString()
        binding!!.artistName.text = artistName
        token = sharePre.getString("token","token").toString()
        followedId = arguments?.getString("id").toString()
        setArtistImg()
        mainViewModel = (activity as MainActivity).mainViewModel
        setUpRecyclerView()
        viewModel.checkIsFollowed(token, FollowedId(followedId))
        viewModel.getTopSongs(artistName)
        viewModel.getAlbum(artistName)
        albumAdapter.setItemClickListener {
           // mainViewModel.unsubscribe(it.name)
            val bundle = Bundle()
            bundle.putString("album",it.name)
            bundle.putString("albumImg",it.imageUrl)
            bundle.putString("artistName",it.artist)
            bundle.putString("albumId",it._id)
            bundle.putString("released",it.released)
            findNavController().navigate(R.id.action_albumFragment_to_songFragment,bundle)
        }
        binding!!.addAndDeleteFollow.setOnClickListener {
            if (binding!!.addAndDeleteFollow.getTag(FILL_HEART) == EMPTY_HEART  ){
                addArtist()
                binding!!.addAndDeleteFollow.setImageResource(FILL_HEART)
                binding!!.addAndDeleteFollow.setTag(FILL_HEART, FILL_HEART)
            }else{
                deleteArtist()
                binding!!.addAndDeleteFollow.setImageResource(EMPTY_HEART)
                binding!!.addAndDeleteFollow.setTag(FILL_HEART, EMPTY_HEART)
            }
            viewModel.shouldUpdateMyArtist(true)
        }
        binding!!.viewAllTopSongs.setOnClickListener {
            findNavController().navigate(R.id.action_albumFragment_to_allTopSongsFragment)
        }
        binding!!.viewAllAlbums.setOnClickListener {
            findNavController().navigate(R.id.action_albumFragment_to_allAlbumsFragment)
        }
        binding!!.artistInfo.setOnClickListener {
            showArtistInfo()
        }
        binding!!.tryAgainBtn.setOnClickListener {
            viewModel.checkIsFollowed(token, FollowedId(followedId))
            viewModel.getTopSongs(artistName)
            viewModel.getAlbum(artistName)
        }

    }
    private fun subscribeToObservers(){
        viewModel.isFollowed.observe(viewLifecycleOwner, Observer { result ->
            when(result.status){
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    result.data?.let {


                        val isFollowed = it.isFavorite
                        if (isFollowed){
                            binding!!.addAndDeleteFollow.setImageResource(FILL_HEART)
                            binding!!.addAndDeleteFollow.setTag(FILL_HEART, FILL_HEART)
                        }else{
                            binding!!.addAndDeleteFollow.setImageResource(EMPTY_HEART)
                            binding!!.addAndDeleteFollow.setTag(FILL_HEART, EMPTY_HEART)
                        }

                    }
                }
            }

        })
        viewModel.albums.observe(viewLifecycleOwner, Observer { response ->
            when(response.status){

                Status.LOADING ->{


                }

                Status.SUCCESS ->{

                    response.data?.let {
                        albumAdapter.differ.submitList(it)
                    }
                }

                Status.ERROR ->{

                    Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                }

            }
        })
        viewModel.topSongs.observe(viewLifecycleOwner, Observer { result ->
            when(result.status){
                Status.LOADING -> {
                    binding!!.networkFailureTxt.visibility = View.GONE
                    binding!!.tryAgainBtn.visibility = View.GONE
//                    binding!!.topSongsTxt.visibility = View.GONE
//                    binding!!.albumsTxt.visibility = View.GONE
//                    binding!!.addAndDeleteFollow.visibility = View.GONE
//                    binding!!.artistInfo.visibility = View.GONE
                    binding!!.artistDetailScrollView.visibility = View.GONE
                    binding!!.loading.visibility = View.VISIBLE
                }
                Status.SUCCESS->{

                    binding!!.artistDetailScrollView.visibility = View.VISIBLE
                    binding!!.loading.visibility = View.GONE

                    result.data?.let {
                        topSongAdapter.differ.submitList(it)
                        topSongAdapter.setOnItemClick { songItem ->
                            val bundle = Bundle()
                            bundle.putSerializable("song",SerializableSong(it))
                            mainViewModel.addCustomAction(bundle,"song")
                            mainViewModel.playOrToggleSong(songItem)
                            findNavController().navigate(R.id.action_albumFragment_to_songPlayingFragment)

                        }
                    }
                }
                Status.ERROR-> {
                   binding!!.loading.visibility = View.GONE
                    binding!!.artistDetailScrollView.visibility = View.GONE
                   binding!!.networkFailureTxt.visibility = View.VISIBLE
                   binding!!.tryAgainBtn.visibility = View.VISIBLE
                }
            }

        })
    }
    private fun addArtist(){
        val artistImg = arguments?.getString("imageUrl").toString()
        val artistName = arguments?.getString("artist").toString()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitInstance.api.selectArtist(token, AddArtist(followedId,artistName,artistImg))
            }catch (t:Throwable){

            }

        }

    }
    private fun deleteArtist(){

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response =RetrofitInstance.api.unSelectArtist(token, ArtistId(followedId))
                Toast.makeText(requireContext(),response.body()?.message,Toast.LENGTH_SHORT).show()
            }catch (t:Throwable){

            }
        }

    }
    private fun setUpRecyclerView(){
        albumAdapter = AlbumAdapter(R.layout.item_album_first,0)
        topSongAdapter = SongAdapter(R.layout.item_top_song,0)
        binding!!.albumRecyclerView.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.HORIZONTAL,false)
        binding!!.albumRecyclerView.adapter = albumAdapter
        binding!!.topSongsRecyclerView.layoutManager = LinearLayoutManager(requireContext(),
            RecyclerView.HORIZONTAL,false)
        binding!!.topSongsRecyclerView.adapter = topSongAdapter

    }
    private fun setArtistImg(){
        val imageUrl = arguments?.getString("imageUrl").toString()
        Glide.with(this).load(imageUrl).into(binding!!.artistDetailImg)
    }
    private fun showArtistInfo(){
        val artistDetailBottomSheet = BottomSheetDialog(requireContext())
        val binding:BottomSheetArtistInfoBinding = BottomSheetArtistInfoBinding.inflate(layoutInflater)
        artistDetailBottomSheet.setContentView(binding.root)
        viewModel.getArtistInfo(followedId)
        viewModel.artistInfo.observe(viewLifecycleOwner, Observer { result ->
            when(result.status){
                Status.LOADING -> {

                }
                Status.SUCCESS->{
                    result.data?.let {
                        binding.artistDescription.text = it.info
                        binding.born.text = "Born: "+it.born
                        binding.height.text = "Height: "+it.height.toString() + "cm"
                    }
                }
                Status.ERROR -> {

                }
            }

        })
        artistDetailBottomSheet.show()
    }
    override fun onDestroy() {

        viewModel.artistInfo.postValue(Resource.clean())
        super.onDestroy()
    }
}