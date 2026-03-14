package com.mleval.pexelsapp.domain.usecase

import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import javax.inject.Inject

class GetCuratedPhotosUseCase @Inject constructor(
    private val pexelsRepository: PexelsRepository
) {

    suspend operator fun invoke(): List<Photo> {
        return pexelsRepository.getCuratedPhotos()
    }
}