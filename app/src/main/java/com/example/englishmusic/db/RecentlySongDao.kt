package com.example.englishmusic.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.englishmusic.model.SongItem

@Dao
interface RecentlySongDao {

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )
    suspend fun upsert(
        recentlySong:SongItem
    ):Long

    @Query(
        "SELECT * FROM recentlySong LIMIT:limit"
    )
    fun getAllRecentlySongs(limit:Int):LiveData<List<SongItem>>


}