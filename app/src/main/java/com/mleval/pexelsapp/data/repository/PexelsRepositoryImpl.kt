package com.mleval.pexelsapp.data.repository

import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(

) : PexelsRepository {

    override suspend fun getCuratedPhotos(): List<Photo> {
        TODO("Not yet implemented")
    }

    override suspend fun getFeaturedCollections(): List<Collection> {
        TODO("Not yet implemented")
    }

    override suspend fun searchPhotos(query: String): List<Photo> {
        TODO("Not yet implemented")
    }
}