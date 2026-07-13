package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streams")
data class StreamItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val url: String,
    val imageUrl: String? = null,
    val isCustom: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val channelNumber: Int = 999
)
