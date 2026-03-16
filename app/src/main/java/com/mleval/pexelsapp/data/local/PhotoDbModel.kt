package com.mleval.pexelsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoDbModel(
    @PrimaryKey val id: Long,
    val imageUrl: String,
    val photographer: String,
    val width: Int,
    val height: Int,
    val cachedAt: Long
)
