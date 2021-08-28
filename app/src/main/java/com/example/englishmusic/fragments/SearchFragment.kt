package com.example.englishmusic.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SearchAdapter
import com.example.englishmusic.databinding.FragmentSearchBinding
import com.example.englishmusic.model.Constance.Companion.SEARCH_MUSIC_DELAY
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private var binding: FragmentSearchBinding? = null


    lateinit var searchAdapter: SearchAdapter


    private lateinit var viewModel: MusicInfoViewModel


    private lateinit var mainViewModel: MainViewModel




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSearchBinding.bind(view)
        viewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRecyclerView()
        subscribeToObservers()

        searchAdapter.setOnItemClickListener {
            when(it.status){
                 "song" -> {
                     val bundle = Bundle().apply {
                         putString("searchSong",it.name)
                     }

                     mainViewModel.addCustomAction(bundle,"searchFragment")



                     CoroutineScope(Dispatchers.Main).launch {
                         delay(500)
                         mainViewModel.mediaItem.observe(viewLifecycleOwner, Observer { respnse ->

                             when(respnse.status){
                                 Status.SUCCESS ->{
                                     respnse.data?.let { songItem ->



                                         mainViewModel.playOrToggleSong(songItem[0])
                                         findNavController().navigate(R.id.action_searchFragment_to_songPlayingFragment)


                                     }
                                 }
                             }

                         })
                     }

















                }
                "artist" -> {
                    val bundle = Bundle().apply {
                        putString("artist",it.name)
                    }
                    findNavController().navigate(R.id.action_searchFragment_to_albumFragment,bundle)
                }
            }





        }






    }


    private fun setUpRecyclerView(){
        searchAdapter =SearchAdapter()
        binding!!.searchRecyclerView.adapter = searchAdapter
        binding!!.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun subscribeToObservers(){
        setUpSearchView()
        viewModel.search.observe(viewLifecycleOwner, Observer { response ->

            when(response.status){
                 Status.SUCCESS ->{
                    response.data?.let {
                        searchAdapter.differ.submitList(it)
                    }
                }
            }

        })
    }


    private fun setUpSearchView(){
        var job:Job? = null
        binding!!.searchView.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_MUSIC_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()){
                        viewModel.getSearch(editable.toString())
                    }
                }
            }

        }
    }




}