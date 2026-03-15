package com.mleval.pexelsapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PexelsDao {

    @Query("SELECT EXISTS(SELECT 1 FROM bookmakers WHERE id = :id)")
    suspend fun isPhotoBookMarked(id: Long): Boolean
    @Query("DELETE FROM bookmakers WHERE id = :id")
    suspend fun removePhotoFromBookMark(id: Long)

    @Query("SELECT *FROM bookmakers")
    fun getPhotosFromBookMark(): Flow<List<BookMarkDbModel>>

    @Query("SELECT * FROM bookmakers WHERE id = :id")
    suspend fun getPhotoFromBookMark(id: Long): BookMarkDbModel

    @Insert(onConflict = REPLACE)
    suspend fun addPhotoToBookMark(photo: BookMarkDbModel)
    @Insert(onConflict = REPLACE)
    suspend fun addCollections(collections: List<CollectionDbModel>)

    @Insert(onConflict = REPLACE)
    suspend fun addPhotos(photos: List<PhotoDbModel>)

    @Query("SELECT * FROM photos")
    fun getAllPhotos(): Flow<List<PhotoDbModel>>

    @Query("SELECT * FROM collections")
    suspend fun getAllCollections(): List<CollectionDbModel>

    @Query("SELECT cachedAt FROM photos LIMIT 1")
    suspend fun getPhotosCacheTime(): Long?

    @Query("SELECT cachedAt FROM collections LIMIT 1")
    suspend fun getCollectionsCacheTime(): Long?

    @Query("DELETE FROM photos")
    suspend fun deleteAllPhotos()

    @Query("DELETE FROM collections")
    suspend fun deleteAllCollections()

    @Transaction
    suspend fun replacePhotos(photos: List<PhotoDbModel>) {

        deleteAllPhotos()

        addPhotos(photos)

    }

    @Transaction
    suspend fun replaceCollections(collections: List<CollectionDbModel>) {

        deleteAllCollections()

        addCollections(collections)

    }
}