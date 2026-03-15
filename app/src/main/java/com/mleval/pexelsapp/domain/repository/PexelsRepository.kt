package com.mleval.pexelsapp.domain.repository

import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import kotlinx.coroutines.flow.Flow

interface PexelsRepository {

    fun getCuratedPhotos(page: Int): Flow<List<Photo>>

    fun searchPhotos(query: String, page: Int): Flow<List<Photo>>

    suspend fun getFeaturedCollections(): List<Collection>

    suspend fun getPhoto(id: Long): Photo

    suspend fun addPhotoToBookMark(photo: Photo)

    suspend fun removePhotoFromBookMark(id: Long)

    suspend fun getPhotoFromBookMark(id: Long): Photo

    fun getPhotosFromBookMark(): Flow<List<Photo>>

    suspend fun isPhotoBookMarked(id: Long): Boolean

}