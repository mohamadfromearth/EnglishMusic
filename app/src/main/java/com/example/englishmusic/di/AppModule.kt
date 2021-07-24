package com.example.englishmusic.di

import android.content.Context
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.adapters.ArtistAdapter
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.exoPlayer.MusicServiceConnection
import com.example.englishmusic.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideArtistAdapter() = ArtistAdapter()


    @Singleton
    @Provides
    fun providesAlbumAdapter() = AlbumAdapter()


    @Singleton
    @Provides
    fun providesSongAdapter() = SongAdapter()




    @Singleton
    @Provides
    fun providesRepository() = MusicRepository()


}