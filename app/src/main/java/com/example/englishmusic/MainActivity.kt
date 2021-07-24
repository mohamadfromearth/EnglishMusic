package com.example.englishmusic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.englishmusic.databinding.ActivityMainBinding
import com.example.englishmusic.model.SongItem
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

 private lateinit var binding: ActivityMainBinding


 private val musicInfoViewModel: MusicInfoViewModel by viewModels()

    private val mainViewModel:MainViewModel by viewModels()






private var curPlayingSong:SongItem? = null

  private var playbackState:PlaybackStateCompat? = null








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

                val nav = findNavController(R.id.musicNavHostFragment)
          binding.buttonNavigation.setupWithNavController(nav)


        mainViewModel.mediaItem.observe(this, Observer {
            it.data?.get(0)?.let { it1 -> mainViewModel.playOrToggleSong(it1) }

        })

        mainViewModel.isConnected.observe(this, Observer {
            Toast.makeText(this,it.hasBeenHandled.toString(),Toast.LENGTH_SHORT).show()
        })


        }




    }







