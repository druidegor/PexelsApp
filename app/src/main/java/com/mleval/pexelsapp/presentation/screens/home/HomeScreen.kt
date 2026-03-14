package com.mleval.pexelsapp.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.mleval.pexelsapp.R
import com.mleval.pexelsapp.domain.entity.Photo

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) {innerPadding ->
        val state by viewModel.state.collectAsState()

        when(val currentState =  state) {
            HomeScreenState.Error -> {

            }
            is HomeScreenState.HomeContent -> {
                HomeContent(
                    modifier= Modifier.padding(innerPadding),
                    state = currentState,
                    onQueryChange = {
                        viewModel.processCommands(HomeScreenCommands.InputQuery(it))
                    },
                    onSearchClick = {
                        viewModel.processCommands(HomeScreenCommands.ClickSearch(it))
                    },
                    onCollectionClick = {
                        viewModel.processCommands(HomeScreenCommands.ToggleCollection(it))
                    }
                )
            }
            HomeScreenState.Loading -> {

            }
        }
    }
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
private fun PhotoImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    onPhotoClick: () -> Unit
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.photo_image),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                onPhotoClick()
            },
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun PhotosGrid(
    modifier: Modifier = Modifier,
    photos: List<Photo>,
    onPhotoClick: () -> Unit
) {

    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {
        items(
            items = photos,
            key = {it.id}
        ) {
            PhotoImage(
                imageUrl = it.imageUrl,
                onPhotoClick = onPhotoClick
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
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TextField(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(50.dp),
            value = state.query,
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
                        onSearchClick(state.query)
                    },
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_icon),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        LazyRow(
            contentPadding = PaddingValues(start = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = state.collections,
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

        PhotosGrid(
            photos = state.photos,
            onPhotoClick = {

            }
        )
    }

}