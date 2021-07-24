package com.example.englishmusic.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishmusic.exoPlayer.MusicService
import com.example.englishmusic.exoPlayer.MusicServiceConnection
import com.example.englishmusic.exoPlayer.currentPlaybackPosition
import com.example.englishmusic.model.Constance.Companion.UPDATE_PLAYER_POSITION_INTERVAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val musicServiceConnection:MusicServiceConnection
) :ViewModel() {

 private val playbackState = musicServiceConnection.playbackState

 private val _curSongDuration = MutableLiveData<Long>()
    val curSongDuration:LiveData<Long> = _curSongDuration


   private val _curPlayerPosition = MutableLiveData<Long>()
     val curPlayerPosition:LiveData<Long> = _curPlayerPosition



    init {
        updateCurrentPlayerPosition()
    }



    private fun updateCurrentPlayerPosition(){
        viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                    _curSongDuration.postValue(MusicService.curSongDuration)
                }
               delay(UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }


}