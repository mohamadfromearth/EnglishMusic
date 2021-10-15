package com.example.englishmusic

import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
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
import com.example.englishmusic.model.DownloadSongSerializable
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.viewmodel.DownloadViewModel
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

 private lateinit var binding: ActivityMainBinding


 private val musicInfoViewModel: MusicInfoViewModel by viewModels()

 val mainViewModel:MainViewModel by viewModels()

  private val downloadViewModel:DownloadViewModel by viewModels()


    @Inject
    lateinit var sharePref:SharedPreferences



 private var songBitmap:Bitmap?=null


private var curPlayingSong:SongItem? = null

  private var playbackState:PlaybackStateCompat? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nav = findNavController(R.id.musicNavHostFragment)
        binding.buttonNavigation.setupWithNavController(nav)



        val bundle = intent.extras
        if (bundle != null ){
            val notif = bundle.getString("fromNotification").toString()
            if(notif == "fromNotification"){
                findNavController(R.id.musicNavHostFragment).navigate(R.id.GlobalActionToSongPlayingFragment)
            }

        }

        postObserversValue()
        subscribeToObservers()
        // navigateToSongPlaying()

        findNavController(R.id.musicNavHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.songPlayingFragment -> {
                    hideBottomBar()
                    binding.buttonNavigation.visibility = View.GONE
                }
                R.id.songPlayingFragmentForDownloads ->{
                    hideBottomBar()
                    binding.buttonNavigation.visibility = View.GONE
                }



                else  -> {
                    binding.buttonNavigation.visibility = View.VISIBLE
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


                if (it.artist == ""){

                    findNavController(R.id.musicNavHostFragment).navigate(R.id.GlobalActionToSongPlayingFragmentForDownload)
                }else{

                    findNavController(R.id.musicNavHostFragment).navigate(R.id.GlobalActionToSongPlayingFragment)
                }

            }
        }

















        }

    override fun onResume() {
        super.onResume()


    }


      private fun subscribeToObservers(){





          mainViewModel.playbackState.observe(this, Observer {
              playbackState = it
              binding.playPauseBottom.setImageResource(
                  if (playbackState?.isPlaying == true) R.drawable.exo_controls_pause else R.drawable.exo_controls_play)
          })


          mainViewModel.curPlayingSong.observe(this, Observer {


              curPlayingSong = it?.toSong()

             it?.let {
                 songBitmap = it.getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON)
                 if (songBitmap != null){

                     Glide.with(this).load(songBitmap).into(binding.bottomCurSongImg)
                 }else{
                     Glide.with(this).load(it.description?.iconUri).into(binding.bottomCurSongImg)
                 }
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



    private fun postObserversValue(){
        val token = sharePref.getString("token","token").toString()
        musicInfoViewModel.getMyArtists(token)
        musicInfoViewModel.getPlayLists()
    }



    }







