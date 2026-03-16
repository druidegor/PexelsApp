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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getFeaturedCollectionsUseCase: GetFeaturedCollectionsUseCase,
    private val searchPhotosUseCase: SearchPhotosUseCase
): ViewModel() {

    private val _state = MutableStateFlow<HomeScreenState>(HomeScreenState.Loading)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()
    private val query = MutableStateFlow<String>("")
    private var page = 1

    init {

        viewModelScope.launch {
            try {
                val collections = getFeaturedCollectionsUseCase().mapIndexed { index, collection ->
                    collection.copy(index = index)
                }
                _state.update {
                    HomeScreenState.HomeContent(
                        photos = emptyList(),
                        collections = collections,
                        query = ""
                    )
                }
                observeQuery()
            } catch (e: IOException) {
                _state.update {
                    HomeScreenState.HomeContent(
                        photos = emptyList(),
                        collections = emptyList(),
                        query = "",
                        isNetworkError = true
                    )
                }
            } catch (e: Exception) {
                sendToast("Something went wrong")
                _state.update { HomeScreenState.Error }
            }

        }

    }

    private fun observeQuery() {
        query
            .onEach { page = 1 }
            .onEach { query ->
                _state.update { previousState ->
                    if (previousState is HomeScreenState.HomeContent) {
                        previousState.copy(
                            query = query,
                            isLoading = true,
                            isNetworkError = false
                        )
                    } else {
                        previousState
                    }
                }
            }
            .flatMapLatest { query ->

                val flow = if (query.isEmpty()) {
                    getCuratedPhotosUseCase(page)
                } else {
                    searchPhotosUseCase(query, page)
                }
                flow.catch { e ->
                    when (e) {
                        is IOException -> {
                            sendToast("No internet connection")
                            _state.update { previousState ->
                                if (previousState is HomeScreenState.HomeContent) {
                                    previousState.copy(
                                        isNetworkError = true,
                                        isLoading = false
                                    )
                                } else previousState
                            }
                        }
                        else -> {
                            sendToast("Something went wrong")
                            _state.update { HomeScreenState.Error }
                        }
                    }
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
                        }.sortedWith (
                            compareByDescending<Collection> {it.isSelected  }
                                .thenBy { it.index }
                        )
                        previousState.copy(photos = photos, collections = newCollections, isLoading = false)
                    } else {
                        previousState
                    }
                }
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
                        val photos = if (command.query.isEmpty()) {

                            getCuratedPhotosUseCase(page).first()

                        } else {

                            searchPhotosUseCase(command.query,page).first()

                        }
                        _state.update { previousState ->

                            if (previousState is HomeScreenState.HomeContent) {

                                previousState.copy(
                                    query = command.query,
                                    photos = photos,
                                    isLoading = false
                                )

                            } else previousState

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
                        }.sortedWith (
                            compareByDescending<Collection> {it.isSelected  }
                                .thenBy { it.index }
                        )
                        previousState.copy(collections = newCollections)
                    } else {
                        previousState
                    }

                }
            }

            HomeScreenCommands.ClearQuery -> {
                query.update { "" }
            }

            HomeScreenCommands.RetryQuery -> {
                query.update { "$it " }
                query.update { it.trim() }
            }
        }
    }

    private suspend fun sendToast(message: String) {
        _events.emit(UiEvent.ShowToast(message))
    }
}

sealed interface HomeScreenCommands{

    data class InputQuery(val query: String): HomeScreenCommands

    data class ClickSearch(val query: String): HomeScreenCommands

    data object ClearQuery: HomeScreenCommands

    data object RetryQuery: HomeScreenCommands

    data class ToggleCollection(val collectionTitle: String): HomeScreenCommands

}
sealed interface HomeScreenState {

    data object Loading: HomeScreenState

    data class HomeContent(
        val query: String = "",
        val photos: List<Photo> = emptyList(),
        val collections: List<Collection> = emptyList(),
        val isLoading: Boolean = false,
        val isNetworkError: Boolean = false
    ): HomeScreenState

    data object Error: HomeScreenState
}

sealed interface UiEvent {

    data class ShowToast(
        val message: String
    ) : UiEvent

}