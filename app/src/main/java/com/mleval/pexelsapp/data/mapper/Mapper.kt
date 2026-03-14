package com.mleval.pexelsapp.data.mapper


import com.mleval.pexelsapp.data.remote.CollectionsDto
import com.mleval.pexelsapp.data.remote.PhotosResponseDto
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo

fun PhotosResponseDto.toDomain(): List<Photo> {
    return this.photos.map {
        Photo(
            id = it.id,
            imageUrl = it.src.medium
        )
    }
}

fun CollectionsDto.toDomain(): List<Collection> {
    return this.collections.map {
        Collection(
            id = it.id,
            title = it.title
        )
    }
}