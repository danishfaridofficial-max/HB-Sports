package com.example.data

import kotlinx.coroutines.flow.Flow

class StreamRepository(private val streamDao: StreamDao) {
    val allStreams: Flow<List<StreamItem>> = streamDao.getAllStreams()
    val customStreams: Flow<List<StreamItem>> = streamDao.getCustomStreams()

    suspend fun insertStream(stream: StreamItem) {
        streamDao.insertStream(stream)
    }

    suspend fun deleteStreamById(id: Int) {
        streamDao.deleteStreamById(id)
    }

    suspend fun deleteNonCustomStreams() {
        streamDao.deleteNonCustomStreams()
    }
}
