package com.example.englishmusic.fragments

import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.englishmusic.MainActivity
import com.example.englishmusic.R
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.FragmentSongPlayBinding
import com.example.englishmusic.databinding.LoginRegisterBottomSheetBinding
import com.example.englishmusic.exoPlayer.isPlaying
import com.example.englishmusic.exoPlayer.toSong
import com.example.englishmusic.exoPlayer.toSongFav
import com.example.englishmusic.model.*
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.example.englishmusic.viewmodel.SongViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.Shapeable
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val FILL_HEART = R.drawable.ic_baseline_favorite_24_red
const val EMPTY_HEART = R.drawable.ic_baseline_favorite_border_24

@AndroidEntryPoint
class SongPlayingFragment:Fragment(R.layout.fragment_song_play) {

    private var binding:FragmentSongPlayBinding? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var mainViewModel: MainViewModel
    private val songViewModel:SongViewModel by viewModels()
    private lateinit var musicInfoViewModel: MusicInfoViewModel

    private var curPlayingSong:SongItem? = null
    private var playbackState: PlaybackStateCompat? = null
    private var shouldUpdateSeekbar = true

    private var myDownloadId:Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSongPlayBinding.bind(view)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        sharedPreferences = (activity as MainActivity).applicationContext.getSharedPreferences("login", Context.MODE_PRIVATE)
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
        binding!!.download.setOnClickListener {
            downloadSong()
        }

    }
    private fun updateTitleAndSongImage(song:SongItem){
        val title = "${song.name} - ${song.artist}"
        binding!!.tvSongName.text = title
        Glide.with(this).load(song.cover).into(binding!!.ivSongImage)
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
                                updateTitleAndSongImage(songs[0])
                                setDurationToTextView(songs[0].duration!!.toLong())
                            }

                        }
                    }
                    else -> Unit

                }


            }
        })
        musicInfoViewModel.isFavorite.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            when(response.status){
                Status.LOADING ->{

                }
                Status.SUCCESS ->{
                    response.data?.let {
                        val isFavorite = it.isFavorite
                        if (isFavorite){

                            binding!!.addAndDeleteFavorite.setImageResource(FILL_HEART)
                            binding!!.addAndDeleteFavorite.setTag(FILL_HEART, FILL_HEART)
                        }else{
                            binding!!.addAndDeleteFavorite.setImageResource(EMPTY_HEART)
                            binding!!.addAndDeleteFavorite.setTag(FILL_HEART, EMPTY_HEART)
                        }


                    }
                }
            }

        })
        mainViewModel.curPlayingSong.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it==null) return@Observer
            curPlayingSong = it.toSong()
            checkIsFavorite(curPlayingSong!!._id)
            binding!!.addAndDeleteFavorite.setOnClickListener {  view ->

                if (binding!!.addAndDeleteFavorite.getTag(FILL_HEART) == EMPTY_HEART  ){
                    binding!!.addAndDeleteFavorite.setImageResource(FILL_HEART)
                    binding!!.addAndDeleteFavorite.setTag(FILL_HEART, FILL_HEART)
                    addToFavorite(it.toSongFav()!!)
                }else{
                    deleteFavorite(curPlayingSong!!._id)
                    binding!!.addAndDeleteFavorite.setImageResource(EMPTY_HEART)
                    binding!!.addAndDeleteFavorite.setTag(FILL_HEART, EMPTY_HEART)
                }
            }
            val duration = it.bundle.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
            binding!!.seekBar.max = duration.toInt()
            setDurationToTextView(duration)
            updateTitleAndSongImage(curPlayingSong!!)


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
    private fun addToFavorite(song: SongItemFav){
        val isLogin =sharedPreferences.getBoolean("isLogin",false)


        val token = sharedPreferences.getString("token","token").toString()

        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response =RetrofitInstance.api.addFavorites(token,song)
                if (response.isSuccessful){
                    Toast.makeText(requireContext(),response.body()?.message,Toast.LENGTH_SHORT).show()
                }
            }catch (t:Throwable){

            }


        }





    }
    private fun deleteFavorite(favoriteId:String){
        val token = sharedPreferences.getString("token","token").toString()
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response =RetrofitInstance.api.deleteFavorite(token, DeleteFavoriteBody(favoriteId))
                if (response.isSuccessful){
                    Toast.makeText(requireContext(),response.body()?.message,Toast.LENGTH_SHORT).show()
                }
            }catch (t:Throwable){

            }


        }


    }

    private fun checkIsFavorite(favoriteId:String){
   val token = sharedPreferences.getString("token","token").toString()
   musicInfoViewModel.checkIsFavorite(token, FavoriteId(favoriteId))

 }
    private fun downloadSong(){
        curPlayingSong?.let {

            val request = DownloadManager.Request(
                Uri.parse(it.songUrl)
            )
                .setTitle(it.name)
                .setDescription(it.artist)
                .setDestinationInExternalFilesDir(requireContext(),Environment.getDataDirectory().absolutePath,it.artist+"-"+it.name+".mp3")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
            val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            myDownloadId =  downloadManager.enqueue(request)
            Toast.makeText(requireContext(),"Song is downloading",Toast.LENGTH_SHORT).show()

            val broadCast = object :BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1)
                    if (id==myDownloadId){
                        Toast.makeText(requireContext(),"Download completed",Toast.LENGTH_SHORT).show()
                    }
                }

            }

            requireActivity().registerReceiver(broadCast, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }


    }






}