package com.example.englishmusic.fragments

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.englishmusic.R
import com.example.englishmusic.databinding.FragmentSongPlayingDownloadBinding
import com.example.englishmusic.exoPlayer.isPlaying
import com.example.englishmusic.exoPlayer.toSong
import com.example.englishmusic.model.song.SongItem
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.example.englishmusic.viewmodel.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class SongPlayingFragmentForDownloads: Fragment(R.layout.fragment_song_playing_download) {
   private var binding:FragmentSongPlayingDownloadBinding?=null

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel:SongViewModel by viewModels()
    private lateinit var musicInfoViewModel:MusicInfoViewModel

    private var curPlayingSong: SongItem?=null
    private var curPlayingSongMetadata:MediaMetadataCompat?=null
    private var playbackState:PlaybackStateCompat?=null
    private var shouldUpdateSeekbar = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongPlayingDownloadBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        subscribeToObserver()
        binding!!.ivPlayPauseDetail.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it,true)
            }
        }
        binding!!.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    setCurPlayerTimeToTextView(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                shouldUpdateSeekbar = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    mainViewModel.seekTo(it.progress.toLong())
                    shouldUpdateSeekbar = true
                }
            }

        })
        binding!!.ivSkipPrevious.setOnClickListener {
            mainViewModel.skipToPreviousSong()
        }
        binding!!.ivSkip.setOnClickListener {
            mainViewModel.skipToNextSong()
        }
    }
    private fun subscribeToObserver(){
        mainViewModel.mediaItem.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {  result ->
                when(result.status){
                    Status.SUCCESS ->{
                        result.data?.let { songs ->
                            if (curPlayingSong == null && songs.isNotEmpty()){
                                curPlayingSong = songs[0]
                                binding!!.seekBar.max = songs[0].duration!!.toInt()
                                //updateTitleAndSongImage(songs[0])
                                setDurationToTextView(songs[0].duration!!.toLong())
                            }

                        }
                    }
                    else -> Unit

                }


            }
        })

        mainViewModel.curPlayingSong.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it==null) return@Observer
            curPlayingSongMetadata = it
            curPlayingSong = it.toSong()

            val duration = it.bundle.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            binding!!.seekBar.max = duration.toInt()
            setDurationToTextView(duration)
            updateTitleAndSongImage(curPlayingSongMetadata!!)

        })
        mainViewModel.playbackState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            playbackState = it
            binding!!.ivPlayPauseDetail.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.exo_controls_pause else R.drawable.exo_controls_play
            )
            binding!!.seekBar.progress = it?.position?.toInt() ?:0
        })
        songViewModel.curPlayerPosition.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (shouldUpdateSeekbar)
                binding!!.seekBar.progress = it.toInt()
            setCurPlayerTimeToTextView(it.toLong())

        })

    }
    private fun updateTitleAndSongImage(song:MediaMetadataCompat){
        val title = song.description.title
        binding!!.tvSongName.text = title
        val songBitmap = song.getBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON)
        Glide.with(requireContext()).load(songBitmap).into(binding!!.ivSongImage)

    }

    private fun setCurPlayerTimeToTextView(ms:Long){
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        binding!!.tvCurTime.text = dateFormat.format(ms)
    }
    private fun setDurationToTextView(ms:Long){
        val dateFormat = SimpleDateFormat("mm:ss",Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        binding!!.tvSongDuration.text = dateFormat.format(ms)
    }
}
