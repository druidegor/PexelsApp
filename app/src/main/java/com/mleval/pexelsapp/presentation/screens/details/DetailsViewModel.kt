package com.mleval.pexelsapp.presentation.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.usecase.AddPhotoToBookMarkUseCase
import com.mleval.pexelsapp.domain.usecase.DownLoadImageUseCase
import com.mleval.pexelsapp.domain.usecase.GetPhotoFromBookMarkUseCase
import com.mleval.pexelsapp.domain.usecase.GetPhotoUseCase
import com.mleval.pexelsapp.domain.usecase.IsPhotoBookMarkedUseCase
import com.mleval.pexelsapp.domain.usecase.RemovePhotoFromBookMarkUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    private val getPhotoUseCase: GetPhotoUseCase,
    private val getPhotoFromBookMarkUseCase: GetPhotoFromBookMarkUseCase,
    private val addPhotoToBookMarkUseCase: AddPhotoToBookMarkUseCase,
    private val removePhotoFromBookMarkUseCase: RemovePhotoFromBookMarkUseCase,
    private val isPhotoBookMarkedUseCase: IsPhotoBookMarkedUseCase,
    private val imageDownloader: DownLoadImageUseCase,
    @Assisted("id") private val id: Long,
    @Assisted("source") private val source: Source
): ViewModel() {

    private val _state = MutableStateFlow<PhotoDetailsState>(PhotoDetailsState.Loading)
    val state = _state.asStateFlow()

    init {
        loadPhoto()
    }

    private fun loadPhoto() {
        viewModelScope.launch {

            _state.value = PhotoDetailsState.Loading

            try {

                val photo = if (source == Source.HOME) {
                    getPhotoUseCase(id)
                } else {
                    getPhotoFromBookMarkUseCase(id)
                }

                val isBookmarked = isPhotoBookMarkedUseCase(id)

                _state.value = PhotoDetailsState.PhotoDetail(
                    imageUrl = photo.imageUrl,
                    photographer = photo.photographer,
                    isBookmarked = isBookmarked
                )

            } catch (e: Exception) {

                _state.value = PhotoDetailsState.Error

            }
        }
    }


    fun processCommand(command: DetailsCommands) {
        viewModelScope.launch {

            val current = state.value

            if (current is PhotoDetailsState.PhotoDetail) {

                when (command) {

                    DetailsCommands.SwitchBookMarkedStatus -> {

                        if (current.isBookmarked) {
                            removePhotoFromBookMarkUseCase(id)
                        } else {
                            addPhotoToBookMarkUseCase(
                                Photo(
                                    id = id,
                                    imageUrl = current.imageUrl,
                                    photographer = current.photographer
                                )
                            )
                        }

                        _state.value = current.copy(
                            isBookmarked = !current.isBookmarked
                        )
                    }

                    DetailsCommands.DownloadPhoto -> {
                        imageDownloader(current.imageUrl)
                    }
                }
            }
        }

    }
    @AssistedFactory
    interface Factory {

        fun create(
            @Assisted("id") id: Long,
            @Assisted("source") source: Source
        ): DetailsViewModel
    }
}

sealed interface DetailsCommands {

    data object DownloadPhoto: DetailsCommands

    data object SwitchBookMarkedStatus: DetailsCommands
}
sealed interface PhotoDetailsState {

    data object Loading : PhotoDetailsState

    data class PhotoDetail(
        val imageUrl: String,
        val photographer: String,
        val isBookmarked: Boolean
    ) : PhotoDetailsState

    data object Error : PhotoDetailsState
}