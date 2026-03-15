package com.mleval.pexelsapp.domain.entity

data class Collection(
    val id: String,
    val title: String,
    val isSelected: Boolean = false,
    val index: Int = 0
)