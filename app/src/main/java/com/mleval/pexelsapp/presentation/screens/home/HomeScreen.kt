@file:Suppress("DEPRECATION")

package com.mleval.pexelsapp.presentation.screens.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.room.util.query
import com.mleval.pexelsapp.R
import com.mleval.pexelsapp.domain.entity.Collection
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.presentation.components.EmptyPhotosStub
import com.mleval.pexelsapp.presentation.components.PhotoCard
import com.mleval.pexelsapp.presentation.components.ProgressBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onPhotoClick: (Long) -> Unit
) {

        val state by viewModel.state.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.events.collect { event ->
                when (event) {
                 is UiEvent.ShowToast -> {
                        Toast.makeText(
                            context,
                            event.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

        when(val currentState =  state) {
            HomeScreenState.Error -> {}
            is HomeScreenState.HomeContent -> {
                HomeContent(
                    modifier = modifier,
                    state = currentState,
                    onQueryChange = {
                        viewModel.processCommands(HomeScreenCommands.InputQuery(it))
                    },
                    onSearchClick = {
                        viewModel.processCommands(HomeScreenCommands.ClickSearch(it))
                    },
                    onCollectionClick = {
                        viewModel.processCommands(HomeScreenCommands.ToggleCollection(it))
                    },
                    onPhotoClick = {
                        onPhotoClick(it)
                    },
                    onRetryClick = {
                        viewModel.processCommands(HomeScreenCommands.RetryQuery)
                    },
                    onClearClick = {
                        viewModel.processCommands(HomeScreenCommands.ClearQuery)
                    }
                )
            }
            HomeScreenState.Loading -> {}
        }
}

@Composable
private fun HomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearchClick: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(50.dp),
        value = query,
        onValueChange = {
            onQueryChange(it)
        },
        singleLine = true,
        placeholder = {
            Text(
                text = stringResource(R.string.search),
                color = MaterialTheme.colorScheme.secondary
            )
        },
        leadingIcon = {
            Icon(
                modifier = Modifier.clickable {
                    onSearchClick(query)
                },
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    modifier = Modifier.clickable {
                        onClearClick()
                    },
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.clear_query),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
@Composable
private fun CollectionChip(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onCollectionClick: (String) -> Unit
) {
    FilterChip(
        modifier = modifier.height(38.dp),
        selected = isSelected,
        shape = RoundedCornerShape(100.dp),
        onClick = {
            onCollectionClick(title)
        },
        label = {
            Text(
                text = title,
                fontSize = 14.sp
            )
        },
        border = null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurface
        )

    )
}

@Composable
private fun CollectionsRow(
    modifier: Modifier = Modifier,
    collections: List<Collection>,
    onCollectionClick: (String) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(collections) {
        lazyListState.scrollToItem(0)
    }

    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(start = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = collections,
            key = { it.id }
        ) {collection ->
            CollectionChip(
                title = collection.title,
                isSelected = collection.isSelected,
                onCollectionClick = {
                    onCollectionClick(collection.title)
                }
            )
        }
    }
}


@Composable
private fun PhotosGrid(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    isLoading: Boolean,
    onPhotoClick: (Long) -> Unit,
) {
    val lazyListState = rememberLazyStaggeredGridState()

    LaunchedEffect(photos) {
        lazyListState.scrollToItem(0)
    }

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        state = lazyListState,
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        itemsIndexed(
            items = photos,
            key = { _, photo -> photo.id }
        ) { index, photo ->

            PhotoCard(
                photo = photo,
                isLoading = isLoading,
                onPhotoClick = {
                    onPhotoClick(it)
                }
            )
        }

    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    state: HomeScreenState.HomeContent,
    onQueryChange: (String) -> Unit,
    onSearchClick: (String) -> Unit,
    onCollectionClick: (String) -> Unit,
    onPhotoClick: (Long) -> Unit,
    onRetryClick: () -> Unit,
    onClearClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        HomeSearchBar(
            query = state.query,
            onQueryChange = onQueryChange,
            onSearchClick = onSearchClick,
            onClearClick = onClearClick
        )
        if (state.collections.isEmpty()) {
            Spacer(Modifier.height(16.dp))
            ProgressBar(isLoading = state.isLoading)

        } else {
            Spacer(Modifier.height(24.dp))
            CollectionsRow(
                collections = state.collections,
                onCollectionClick = onCollectionClick
            )
            Spacer(Modifier.height(9.dp))
            ProgressBar(isLoading = state.isLoading)
        }
        Spacer(Modifier.height(12.dp))
        when {
            state.isNetworkError -> {
                NetworkStub(
                    onRetryClick = onRetryClick
                )
            }
            state.photos.isEmpty() && !state.isLoading -> {
                EmptyPhotosStub(
                    text = stringResource(R.string.no_results_found),
                    onExploreClick = onClearClick
                )
            }
            else -> {
                PhotosGrid(
                    photos = state.photos,
                    isLoading = state.isLoading,
                    onPhotoClick = onPhotoClick
                )
            }
        }

    }

}

@Composable
private fun NetworkStub(
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit
) {
    Column (
        modifier=modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Icon(
            painter = painterResource(R.drawable.ic_no_network),
            contentDescription = stringResource(R.string.no_internet),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            modifier = Modifier.clickable {
                onRetryClick()
            },
            text = stringResource(R.string.try_again),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700,
            fontSize = 18.sp
        )
    }
}
