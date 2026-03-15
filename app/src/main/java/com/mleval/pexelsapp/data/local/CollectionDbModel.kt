package com.mleval.pexelsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionDbModel(
    @PrimaryKey val id: String,
    val title: String,
    val cachedAt: Long
)
