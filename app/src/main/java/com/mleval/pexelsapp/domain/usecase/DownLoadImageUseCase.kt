package com.mleval.pexelsapp.domain.usecase

import com.mleval.pexelsapp.data.downloader.DownLoader
import javax.inject.Inject

class DownLoadImageUseCase @Inject constructor(
    private val imageDownloader: DownLoader
) {
    operator fun invoke(url: String): Long {
        return imageDownloader.download(url)
    }
}