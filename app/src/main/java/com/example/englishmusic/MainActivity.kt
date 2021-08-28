package com.example.englishmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.englishmusic.databinding.ActivityMainBinding
import com.example.englishmusic.exoPlayer.isPlaying
import com.example.englishmusic.exoPlayer.toSong
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

 private lateinit var binding: ActivityMainBinding


 private val musicInfoViewModel: MusicInfoViewModel by viewModels()

 val mainViewModel:MainViewModel by viewModels()






private var curPlayingSong:SongItem? = null

  private var playbackState:PlaybackStateCompat? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
















        }

    override fun onResume() {
        super.onResume()
        val nav = findNavController(R.id.musicNavHostFragment)
        binding.buttonNavigation.setupWithNavController(nav)

        findNavController(R.id.musicNavHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.songPlayingFragment -> hideBottomBar()
                R.id.songFragment -> showBottomBar()
                else  -> {
                    if (curPlayingSong!= null) showBottomBar()
                    if (curPlayingSong == null) hideBottomBar()
                }
            }
        }

        val bundle = intent.extras
        if (bundle != null ){
            val notif = bundle.getString("fromNotification").toString()
            if(notif == "fromNotification"){
                findNavController(R.id.musicNavHostFragment).navigate(R.id.GlobalActionToSongPlayingFragment)
            }

        }
        subscribeToObservers()
        // navigateToSongPlaying()

        findNavController(R.id.musicNavHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.songPlayingFragment -> hideBottomBar()
                R.id.songFragment -> showBottomBar()
                else  -> {
                    if (curPlayingSong!= null) showBottomBar()
                    if (curPlayingSong == null) hideBottomBar()
                }
            }
        }


        binding.playPauseBottom.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)
            }

        }

        binding.bottomPlayer.setOnClickListener {
            curPlayingSong?.let {
                findNavController(R.id.musicNavHostFragment).navigate(R.id.GlobalActionToSongPlayingFragment)
            }
        }


    }


      private fun subscribeToObservers(){
          mainViewModel.mediaItem.observe(this, Observer {

          })


          mainViewModel.curPlayingSong.observe(this, Observer {
              it?.let {
                  binding.bottomArtistName.text = it.description.subtitle
                  binding.songNameBottom.text = it.description.title
                  Glide.with(this).load(it.description.iconUri).into(binding.bottomCurSongImg)
              }

          })

          mainViewModel.playbackState.observe(this, Observer {
              playbackState = it
              binding.playPauseBottom.setImageResource(
                  if (playbackState?.isPlaying == true) R.drawable.exo_controls_pause else R.drawable.exo_controls_play)
          })


          mainViewModel.curPlayingSong.observe(this, Observer {


              curPlayingSong = it?.toSong()

             it?.let {



                 Glide.with(this).load(it.description?.iconUri).into(binding.bottomCurSongImg)
                 binding.songNameBottom.text = it.description.title
                 binding.bottomArtistName.text = it.description.subtitle

             }



          })

      }


    private fun hideBottomBar(){
        binding.bottomPlayer.visibility = View.GONE
    }

    private fun showBottomBar(){
        binding.bottomPlayer.visibility = View.VISIBLE
    }







    }







