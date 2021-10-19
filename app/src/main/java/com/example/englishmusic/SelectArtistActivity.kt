package com.example.englishmusic

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.englishmusic.adapters.SelectArtistsAdapter
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.ActivitySelectArtistBinding
import com.example.englishmusic.model.artist.AddArtist
import com.example.englishmusic.model.artist.ArtistId
import com.example.englishmusic.model.Constance
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SelectArtistActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySelectArtistBinding
    @Inject
    lateinit var sharePref:SharedPreferences
    private lateinit var selectArtistAdapter: SelectArtistsAdapter
    private val viewModel:MusicInfoViewModel by viewModels()
    private var token = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectArtistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        token = sharePref.getString("token","token").toString()
        viewModel.getArtist()
        setUpRecyclerView()
        subscribeToObservers()
        selectArtistAdapter.setOnItemClickListener { artistItem, isSelected ->
            if (isSelected) {
                deleteArtist(artistItem._id)
            }
            else{
                addArtist(artistItem._id,artistItem.name,artistItem.artistImg)
            }
        }
        binding.nextBtn.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            val editor = sharePref.edit()
            editor.putBoolean(Constance.IS_ARTIST_SELECTED,true)
            editor.apply()
            startActivity(intent)
            finish()
        }
    }
    private fun setUpRecyclerView(){
        selectArtistAdapter = SelectArtistsAdapter()
        binding.selectArtistsRecyclerView.layoutManager = GridLayoutManager(this,3)
        binding.selectArtistsRecyclerView.adapter = selectArtistAdapter
    }
    private fun subscribeToObservers(){
        viewModel.Singer.observe(this, Observer { result ->
            when(result.status){
                Status.LOADING -> {

                }
                Status.SUCCESS-> {
                    result.data?.let {
                        selectArtistAdapter.differ.submitList(it)
                    }
                }
                Status.ERROR-> {

                }
            }

        })
    }
    private fun addArtist(artistId: String,name:String,artistImg:String){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitInstance.api.selectArtist(token, AddArtist(artistId,name,artistImg))
            }catch (t:Throwable){
            }
        }
    }
    private fun deleteArtist(artistId:String){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RetrofitInstance.api.unSelectArtist(token, ArtistId(artistId))
            }catch (t:Throwable){

            }
        }
    }
}