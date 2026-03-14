package com.mleval.pexelsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectionsDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String
)
