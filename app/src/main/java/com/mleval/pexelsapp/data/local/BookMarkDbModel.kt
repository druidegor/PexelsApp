package com.mleval.pexelsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmakers")
data class BookMarkDbModel(
    @PrimaryKey val id: Long,
    val imageUrl: String,
    val photographer: String,
)
