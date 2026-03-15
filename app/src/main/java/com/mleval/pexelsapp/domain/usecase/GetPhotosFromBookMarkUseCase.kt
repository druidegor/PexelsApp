package com.mleval.pexelsapp.domain.usecase

import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.repository.PexelsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosFromBookMarkUseCase @Inject constructor(
    private val pexelsRepository: PexelsRepository
) {

    suspend operator fun invoke(): Flow<List<Photo>> {
        return pexelsRepository.getPhotosFromBookMark()
    }
}