package com.mleval.pexelsapp.presentation.screens.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mleval.pexelsapp.R
import com.mleval.pexelsapp.domain.entity.Photo
import com.mleval.pexelsapp.presentation.components.EmptyPhotosStub
import com.mleval.pexelsapp.presentation.components.PhotoCard
import com.mleval.pexelsapp.presentation.components.ProgressBar


@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarksViewModel = hiltViewModel(),
    onClick: () -> Unit,
    onPhotoClick: (Long) -> Unit
) {

    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.bookmarks),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        ProgressBar(isLoading = state.isLoading)
        if (!state.isLoading && state.photos.isEmpty()) {
            EmptyPhotosStub(
                text = stringResource(R.string.you_haven_t_saved_anything_yet)
            ) {
                onClick()
            }
        } else {
            BookmarksGrid(
                state = state,
                isLoading = state.isLoading,
                onPhotoClick = {
                        onPhotoClick(it)
                }
            )
        }
    }
}

@Composable
private fun PhotoImageWithAuthor(
    modifier: Modifier = Modifier,
    photo: Photo,
    isLoading: Boolean,
    onPhotoClick: (Long) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
    ) {

        PhotoCard(
            photo = photo,
            isLoading = isLoading,
            onPhotoClick = onPhotoClick
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = photo.photographer,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(12.dp)
            )
        }
    }
}

@Composable
fun BookmarksGrid(
    modifier: Modifier = Modifier,
    state: BookmarksState,
    isLoading: Boolean,
    onPhotoClick: (Long) -> Unit
) {
    var visibleItems by rememberSaveable {
        mutableIntStateOf(30)
    }

    val visiblePhotos = state.photos.take(visibleItems)


    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        columns = StaggeredGridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp
    ) {

        itemsIndexed(
            items = visiblePhotos,
            key = { _, photo -> photo.id }
        ) { index, photo ->

            if (index >= visibleItems - 5) {
                visibleItems += 30
            }

            PhotoImageWithAuthor(
                photo = photo,
                isLoading = isLoading,
                onPhotoClick = onPhotoClick
            )
        }
    }
}