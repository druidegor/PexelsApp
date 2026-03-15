package com.mleval.pexelsapp.data.remote


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApiService {

    @GET("curated")
    suspend fun loadCuratedPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PhotosResponseDto

    @GET("collections/featured")
    suspend fun loadFeaturedCollections(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 7
    ): CollectionsDto

    @GET("search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PhotosResponseDto

    @GET("photos/{id}")
    suspend fun loadPhotoDetails(
        @Path("id") id: Long
    ): PhotoDto
}