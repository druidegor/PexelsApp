package com.mleval.pexelsapp.data.mapper


import com.mleval.pexelsapp.data.local.BookMarkDbModel
import com.mleval.pexelsapp.data.local.CollectionDbModel
import com.mleval.pexelsapp.data.local.PhotoDbModel
import com.mleval.pexelsapp.data.remote.CollectionsDto
import com.mleval.pexelsapp.data.remote.PhotoDto
import com.mleval.pexelsapp.data.remote.PhotosResponseDto
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo

fun PhotoDto.toPhotoDomain(): Photo {
    return Photo(
        id = id,
        imageUrl = src.larger,
        photographer = photographer,
        width = width,
        height = height
    )
}
fun PhotosResponseDto.toPhotoDbModel(time: Long): List<PhotoDbModel> {
    return photos.map {
        PhotoDbModel(
            id = it.id,
            imageUrl = it.src.larger,
            photographer = it.photographer,
            cachedAt = time,
            width = it.width,
            height = it.height
        )
    }
}

fun CollectionsDto.toCollectionDbModel(time: Long): List<CollectionDbModel> {
    return collections.map {
        CollectionDbModel(
            id = it.id,
            title = it.title,
            cachedAt = time
        )
    }
}

fun List<PhotoDbModel>.toPhotoDomain(): List<Photo> {
    return map {
        Photo(
            id = it.id,
            photographer = it.photographer,
            imageUrl = it.imageUrl,
            width = it.width,
            height = it.height
        )
    }
}

fun List<CollectionDbModel>.toCollectionDomain(): List<Collection> {
    return map {
        Collection(
            id = it.id,
            title = it.title
        )
    }
}
fun PhotosResponseDto.toDomain(): List<Photo> {
    return this.photos.map {
        Photo(
            id = it.id,
            imageUrl = it.src.larger,
            photographer = it.photographer,
            width = it.width,
            height = it.height
        )
    }
}

fun Photo.toBookMarkPhoto(): BookMarkDbModel {
    return BookMarkDbModel(
        id = id,
        imageUrl = imageUrl,
        photographer = photographer,
        width = width,
        height = height
    )
}

fun BookMarkDbModel.toPhoto(): Photo {
    return Photo(
        id= id,
        imageUrl = imageUrl,
        photographer = photographer,
        width = width,
        height = height
    )
}