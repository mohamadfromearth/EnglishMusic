package com.example.englishmusic.fragments
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.databinding.FragmentAllAlbumsBinding
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MusicInfoViewModel
class AllAlbumsFragment: Fragment(R.layout.fragment_all_albums) {
    private lateinit var musicInfoViewModel: MusicInfoViewModel
    private var binding:FragmentAllAlbumsBinding? = null
    private lateinit var allAlbumAdapter: AlbumAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllAlbumsBinding.bind(view)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        setUpRecyclerView()
        subscribeToObservers()
        allAlbumAdapter.setItemClickListener {
            val bundle = Bundle()
            bundle.putString("album",it.name)
            bundle.putString("albumImg",it.imageUrl)
            bundle.putString("artistName",it.artist)
            bundle.putString("albumId",it._id)
            bundle.putString("released",it.released)
            findNavController().navigate(R.id.action_allAlbumsFragment_to_songFragment,bundle)

        }
    }
    private fun subscribeToObservers(){
        musicInfoViewModel.albums.observe(viewLifecycleOwner, Observer { result ->
            when(result.status){
                Status.SUCCESS->{
                    result.data?.let {
                        allAlbumAdapter.differ.submitList(it)
                    }
                }
            }

        })
    }
    private fun setUpRecyclerView(){
        allAlbumAdapter = AlbumAdapter(R.layout.item_album,0)
        binding!!.allAlbumRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding!!.allAlbumRecyclerView.adapter = allAlbumAdapter
    }
}