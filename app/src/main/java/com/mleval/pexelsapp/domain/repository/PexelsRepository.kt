package com.mleval.pexelsapp.domain.repository

import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import kotlinx.coroutines.flow.Flow

interface PexelsRepository {

    suspend fun getCuratedPhotos(): Flow<List<Photo>>

    suspend fun searchPhotos(query: String): Flow<List<Photo>>

    suspend fun getFeaturedCollections(): List<Collection>
}