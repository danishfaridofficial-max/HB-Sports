package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StreamDao {
    @Query("SELECT * FROM streams ORDER BY isCustom ASC, channelNumber ASC, id ASC")
    fun getAllStreams(): Flow<List<StreamItem>>

    @Query("SELECT * FROM streams WHERE isCustom = 1 ORDER BY timestamp DESC")
    fun getCustomStreams(): Flow<List<StreamItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStream(stream: StreamItem)

    @Query("DELETE FROM streams WHERE id = :id")
    suspend fun deleteStreamById(id: Int)

    @Query("DELETE FROM streams WHERE isCustom = 0")
    suspend fun deleteNonCustomStreams()
}
