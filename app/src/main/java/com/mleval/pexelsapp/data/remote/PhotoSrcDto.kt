package com.mleval.pexelsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSrcDto(
    @SerialName("medium") val medium: String
)
