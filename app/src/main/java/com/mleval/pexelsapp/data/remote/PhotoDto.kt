package com.mleval.pexelsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    @SerialName("id") val id: Long,
    @SerialName("src") val src: PhotoSrcDto,
    @SerialName("photographer") val photographer: String
)
