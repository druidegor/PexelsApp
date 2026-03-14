package com.mleval.pexelsapp.data.repository

import android.util.Log
import com.mleval.pexelsapp.data.mapper.toDomain
import com.mleval.pexelsapp.data.remote.PexelsApiService
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(
    private val pexelsApi: PexelsApiService
) : PexelsRepository {

    override suspend fun getCuratedPhotos(): Flow<List<Photo>> {
        return flow {
            val result = pexelsApi.loadCuratedPhotos().toDomain()
            emit(result)
        }
    }

    override suspend fun getFeaturedCollections(): List<Collection> {
        return pexelsApi.loadFeaturedCollections()
            .also {
                Log.d("ViewModel", "collections loading ${this}")
            }
            .toDomain()
    }

    override suspend fun searchPhotos(query: String): Flow<List<Photo>> {
        return flow {
            val result = pexelsApi.searchPhotos(query).toDomain()
            emit(result)
        }
    }
}