package com.mleval.pexelsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosResponseDto(
    @SerialName("photos") val photos: PhotoDto
)
