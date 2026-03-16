package com.mleval.pexelsapp.domain.entity

data class Photo(
    val id: Long,
    val imageUrl: String,
    val photographer: String,
    val width: Int,
    val height: Int
)
