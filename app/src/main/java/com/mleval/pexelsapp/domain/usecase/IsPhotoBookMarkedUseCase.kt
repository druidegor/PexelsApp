package com.mleval.pexelsapp.domain.usecase

import com.mleval.pexelsapp.domain.repository.PexelsRepository
import javax.inject.Inject

class IsPhotoBookMarkedUseCase @Inject constructor(
    private val pexelsRepository: PexelsRepository
) {

    suspend operator fun invoke(id: Long): Boolean {
        return pexelsRepository.isPhotoBookMarked(id)
    }
}