package com.mleval.pexelsapp.domain.entity

data class Collection(
    val id: Int,
    val title: String,
    val isSelected: Boolean = false
)