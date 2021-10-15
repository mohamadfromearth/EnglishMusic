package com.example.englishmusic.di

import android.content.Context
import androidx.room.Room
import com.example.englishmusic.adapters.AlbumAdapter
import com.example.englishmusic.adapters.ArtistAdapter
import com.example.englishmusic.adapters.SearchAdapter
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.api.MusicApi
import com.example.englishmusic.db.RecentlySongDao
import com.example.englishmusic.db.RecentlySongDataBase
import com.example.englishmusic.exoPlayer.MusicServiceConnection
import com.example.englishmusic.model.Constance.Companion.BASE_URL
import com.example.englishmusic.model.Constance.Companion.DATA_BASE_NAME
import com.example.englishmusic.model.Constance.Companion.LOGIN_SHARE_PREF
import com.example.englishmusic.repository.MusicRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context,
        recentlySongDataBase: RecentlySongDao
    ) = MusicServiceConnection(context,recentlySongDataBase)




    @Singleton
    @Provides
    fun provideContext(
        @ApplicationContext context: Context
    ) = context




/*    @Singleton
    @Provides
    fun providesAlbumAdapter() = AlbumAdapter()*/


   /* @Singleton
    @Provides
    fun providesSongAdapter() = SongAdapter()*/

    @Singleton
    @Provides
    fun providesSearchAdapter() = SearchAdapter()




    @Singleton
    @Provides
    fun providesRepository() = MusicRepository()


     @Singleton
     @Provides
     fun provideRoomDb(@ApplicationContext context:Context)
     = Room.databaseBuilder(context,RecentlySongDataBase::class.java,DATA_BASE_NAME).build()


    @Singleton
    @Provides
    fun providesRecentlyDao(recentlyDataBase:RecentlySongDataBase) =
        recentlyDataBase.gerRecentlySongDao()


    @Singleton
    @Provides
    fun providesSharePref(@ApplicationContext context: Context) = context.applicationContext.getSharedPreferences(LOGIN_SHARE_PREF,Context.MODE_PRIVATE)








}