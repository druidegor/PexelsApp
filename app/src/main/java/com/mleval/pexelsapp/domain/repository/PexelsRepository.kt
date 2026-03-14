package com.mleval.pexelsapp.domain.repository

import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo

interface PexelsRepository {

    suspend fun getCuratedPhotos(): List<Photo>

    suspend fun searchPhotos(query: String): List<Photo>

    suspend fun getFeaturedCollections(): List<Collection>
}