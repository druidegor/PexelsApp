package com.mleval.pexelsapp.presentation.screens.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.usecase.GetPhotosFromBookMarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val getPhotosFromBookMarkUseCase: GetPhotosFromBookMarkUseCase
): ViewModel() {
    private val _state = MutableStateFlow(BookmarksState())
    val state = _state.asStateFlow()

    init {
        getPhotosFromBookMarkUseCase()
            .onStart {
                _state.update {
                    it.copy(isLoading = true)
                }
            }
            .onEach { photos ->

                _state.update {
                    it.copy(
                        photos = photos,
                        isLoading = false
                    )
                }

            }
            .catch {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }


}

data class BookmarksState(
    val photos: List<Photo> = emptyList(),
    val isLoading: Boolean = false
)