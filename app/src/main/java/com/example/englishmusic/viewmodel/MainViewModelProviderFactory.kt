package com.example.englishmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.englishmusic.exoPlayer.MusicServiceConnection

class MainViewModelProviderFactory(
    val musicServiceConnection: MusicServiceConnection
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(musicServiceConnection) as T
    }

}