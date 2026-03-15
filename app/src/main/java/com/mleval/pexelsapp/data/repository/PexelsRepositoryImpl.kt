package com.mleval.pexelsapp.data.repository

import com.mleval.pexelsapp.data.local.PexelsDao
import com.mleval.pexelsapp.data.mapper.toBookMarkPhoto
import com.mleval.pexelsapp.data.mapper.toCollectionDbModel
import com.mleval.pexelsapp.data.mapper.toCollectionDomain
import com.mleval.pexelsapp.data.mapper.toDomain
import com.mleval.pexelsapp.data.mapper.toPhoto
import com.mleval.pexelsapp.data.mapper.toPhotoDbModel
import com.mleval.pexelsapp.data.mapper.toPhotoDomain
import com.mleval.pexelsapp.data.remote.PexelsApiService
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PexelsRepositoryImpl @Inject constructor(
    private val pexelsApi: PexelsApiService,
    private val pexelsDao: PexelsDao
) : PexelsRepository {

    private val CACHE_TTL = 60 * 60 * 1000L

    override suspend fun getPhoto(id: Long): Photo {
        return pexelsApi.loadPhotoDetails(id).toPhotoDomain()
    }


    override fun getCuratedPhotos(page: Int): Flow<List<Photo>> = flow {

        val cacheTime = pexelsDao.getPhotosCacheTime()

        val isCacheValid = cacheTime != null &&
                System.currentTimeMillis() - cacheTime < CACHE_TTL

        if (!isCacheValid) {
            loadCuratedPhotos(page)
        }
        emitAll(
            pexelsDao.getAllPhotos().map {
                it.toPhotoDomain()
            }
        )
    }

    override suspend fun getFeaturedCollections(): List<Collection> {
        val cacheTime = pexelsDao.getCollectionsCacheTime()

        val isCacheValid = cacheTime != null &&
                System.currentTimeMillis() - cacheTime < CACHE_TTL

        if (!isCacheValid) {
            loadFeaturedCollections()
        }
        return pexelsDao.getAllCollections().toCollectionDomain()
    }

    override fun searchPhotos(query: String, page: Int): Flow<List<Photo>> {
        return flow {
            val result = pexelsApi.searchPhotos(query, page).toDomain()
            emit(result)
        }
    }

    private suspend fun loadCuratedPhotos(page: Int) {
        try{
            val time = System.currentTimeMillis()
            val photos = pexelsApi.loadCuratedPhotos(page).toPhotoDbModel(time)

            pexelsDao.replacePhotos(photos)

        } catch (e: Exception) {

        }
    }

    private suspend fun loadFeaturedCollections() {
        try{
            val time = System.currentTimeMillis()
            val collections = pexelsApi.loadFeaturedCollections().toCollectionDbModel(time)

            pexelsDao.replaceCollections(collections)

        } catch (e: Exception) {

        }
    }

    override suspend fun addPhotoToBookMark(photo: Photo) {
        pexelsDao.addPhotoToBookMark(photo.toBookMarkPhoto())
    }

    override suspend fun getPhotoFromBookMark(id: Long): Photo {
        return pexelsDao.getPhotoFromBookMark(id).toPhoto()
    }

    override fun getPhotosFromBookMark(): Flow<List<Photo>> {
        return pexelsDao.getPhotosFromBookMark().map { list ->
            list.map { it.toPhoto() }
        }
    }

    override suspend fun removePhotoFromBookMark(id: Long) {
        pexelsDao.removePhotoFromBookMark(id)
    }

    override suspend fun isPhotoBookMarked(id: Long): Boolean {
        return pexelsDao.isPhotoBookMarked(id)
    }
}