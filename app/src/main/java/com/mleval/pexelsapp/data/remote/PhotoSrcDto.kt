package com.mleval.pexelsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSrcDto(
    @SerialName("large2x") val larger: String
)
