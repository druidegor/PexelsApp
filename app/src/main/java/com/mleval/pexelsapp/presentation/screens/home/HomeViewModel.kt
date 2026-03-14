@file:OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)

package com.mleval.pexelsapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.domain.usecase.GetCuratedPhotosUseCase
import com.mleval.pexelsapp.domain.usecase.GetFeaturedCollectionsUseCase
import com.mleval.pexelsapp.domain.usecase.SearchPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val searchPhotosUseCase: SearchPhotosUseCase
): ViewModel() {

    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state = _state.asStateFlow()

    private val query = MutableStateFlow<String>("")

    init {

        viewModelScope.launch {
            try {
                val collections = getFeaturedCollectionsUseCase()
                _state.update {
                    HomeScreenState.HomeContent(
                        photos = emptyList(),
                        collections = collections,
                        query = ""
                    )
                }
            } catch (e: Exception) {
                _state.update { HomeScreenState.Error }
            }

        }

        query
            .debounce(500)
            .distinctUntilChanged()
            .onEach { query ->
                _state.update { previousState ->
                    if (previousState is HomeScreenState.HomeContent) {
                        previousState.copy(query = query, isLoading = true)
                    } else {
                        previousState
                    }
                }
            }
            .flatMapLatest { query ->
                if (query.isEmpty()) {
                    getCuratedPhotosUseCase()
                } else {
                    searchPhotosUseCase(query)
                }
            }
            .onEach {photos ->
                _state.update { previousState ->
                    if (previousState is HomeScreenState.HomeContent) {
                        val newCollections = previousState.collections.map {
                            if (it.title == previousState.query) {
                                it.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        }
                        previousState.copy(photos = photos, collections = newCollections, isLoading = false)
                    } else {
                        previousState
                    }
                }
            }
            .catch {
                _state.update { HomeScreenState.Error }
            }
            .launchIn(viewModelScope)
    }

    fun processCommands(command: HomeScreenCommands) {
        when(command) {
            is HomeScreenCommands.ClickSearch -> {
                viewModelScope.launch {

                    try {
                        _state.update { previousState ->
                            if (previousState is HomeScreenState.HomeContent) {
                                previousState.copy(isLoading = true)
                            } else previousState
                        }
                        searchPhotosUseCase(command.query).collect { photos ->
                            _state.update { previousState ->
                                if (previousState is HomeScreenState.HomeContent) {
                                    previousState.copy(
                                        query = command.query,
                                        photos = photos,
                                        isLoading = false
                                    )
                                } else {
                                    previousState
                                }

                            }
                        }

                    } catch (e: Exception) {
                        _state.update { HomeScreenState.Error }
                    }
                }
            }
            is HomeScreenCommands.InputQuery -> {
                query.update {
                    command.query
                }
            }
            is HomeScreenCommands.ToggleCollection -> {
                query.update { command.collectionTitle }
                _state.update { previousState ->
                    if (previousState is HomeScreenState.HomeContent) {
                        val newCollections = previousState.collections.map {
                            if (it.title == command.collectionTitle) {
                                it.copy(isSelected = true)
                            } else {
                                it.copy(isSelected = false)
                            }
                        }
                        previousState.copy(collections = newCollections)
                    } else {
                        previousState
                    }

                }
            }
        }
    }
}

sealed interface HomeScreenCommands{

    data class InputQuery(val query: String): HomeScreenCommands

    data class ClickSearch(val query: String): HomeScreenCommands

    data class ToggleCollection(val collectionTitle: String): HomeScreenCommands
}
sealed interface HomeScreenState {

    object Loading: HomeScreenState

    data class HomeContent(
        val query: String = "",
        val photos: List<Photo> = emptyList(),
        val collections: List<Collection> = emptyList(),
        val isLoading: Boolean = false
    ): HomeScreenState

    object Error: HomeScreenState
}