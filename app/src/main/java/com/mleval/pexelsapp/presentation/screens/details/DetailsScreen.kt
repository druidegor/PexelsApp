@file:OptIn(ExperimentalMaterial3Api::class)

package com.mleval.pexelsapp.presentation.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mleval.pexelsapp.R
import com.mleval.pexelsapp.presentation.components.ProgressBar

@Composable
fun DetailsScreen(
    modifier: Modifier = Modifier,
    id: Long,
    source: Source,
    viewModel: DetailsViewModel = hiltViewModel(
        creationCallback = {factory: DetailsViewModel.Factory ->
            factory.create(id = id, source = source)

        }
    ),
    onNavigationClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    when(val current = state) {
        PhotoDetailsState.Error -> {
            EmptyPhotosStub {
                onNavigationClick()
            }
        }
        PhotoDetailsState.Loading -> {
            LoadingScreen {
                onNavigationClick()
            }
        }
        is PhotoDetailsState.PhotoDetail -> {
            DetailsContent(
                state = current,
                onNavigationClick = onNavigationClick,
                onDownloadClick = {
                    viewModel.processCommand(DetailsCommands.DownloadPhoto)
                },
                onBookMarkClick = {
                    viewModel.processCommand(DetailsCommands.SwitchBookMarkedStatus)
                }
            )
        }
    }
}

@Composable
private fun EmptyPhotosStub(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DetailsTopAppBar(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                onNavigationClick = {
                    onNavigationClick()
                }
            )
        }
    ) {  innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.no_results_found),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                modifier = Modifier.clickable {
                    onNavigationClick()
                },
                text = stringResource(R.string.explore),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DetailsTopAppBar(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                onNavigationClick = {
                    onNavigationClick()
                }
            )
        }
    ) {innerPadding ->

        ProgressBar(
            modifier = modifier.padding(innerPadding),
            isLoading = true
        )

    }
}

@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    state: PhotoDetailsState.PhotoDetail,
    onNavigationClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onBookMarkClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DetailsTopAppBar(
                modifier = Modifier
                    .padding(horizontal = 24.dp),
                photographer = state.photo.photographer,
                onNavigationClick = {
                    onNavigationClick()
                }
            )
        }
    ) {innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            PhotoWithZoom(
                imageUrl = state.photo.imageUrl
            )
            Spacer(Modifier.weight(1f))
                DetailsBottomBar(
                    onDownloadClick =  {
                        onDownloadClick()
                    },
                    isBookmarked = state.isBookmarked,
                    onBookMarkClick = {
                        onBookMarkClick()
                    }
                )
        }
    }

}

@Composable
private fun PhotoWithZoom(
    modifier: Modifier = Modifier,
    imageUrl: String
) {
    var scale by remember {
        mutableStateOf(1f)
    }
    var offset by remember {
        mutableStateOf(Offset.Zero)
    }
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
            scale = (scale * zoomChange).coerceIn(1f,5f)

            val extraWidth = (scale - 1) * constraints.maxWidth
            val extraHeigh = (scale-1) * constraints.maxHeight

            val maxX = extraWidth/2
            val maxY = extraHeigh/2

            offset = Offset(
                x = (offset.x + panChange.x).coerceIn(-maxX,maxX),
                y = (offset.y + panChange.y).coerceIn(-maxY,maxY)
            )
        }
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.image),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .clip(RoundedCornerShape(16.dp))
                .transformable(state)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        do {
                            val event = awaitPointerEvent()
                        } while (event.changes.any { it.pressed })

                        scale = 1f
                        offset = Offset.Zero
                    }
                }
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit,

            )
    }

}

@Composable
private fun DetailsTopAppBar(
    modifier: Modifier = Modifier,
    photographer: String = "",
    onNavigationClick: () -> Unit
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = photographer,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable {
                        onNavigationClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.get_back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    )
}

@Composable
private fun DetailsBottomBar(
    modifier: Modifier = Modifier,
    isBookmarked: Boolean,
    onDownloadClick: () -> Unit,
    onBookMarkClick: () -> Unit
) {
    Row (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface),
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = { onDownloadClick() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.download_image),
                    tint = Color.White
                )
            }

            Text(
                modifier = Modifier.padding(start = 24.dp, end = 48.dp),
                text = stringResource(R.string.download),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable {
                    onBookMarkClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = if (isBookmarked) {
                    if (isSystemInDarkTheme()) {
                        painterResource(R.drawable.ic_bookmark_active_dark)
                    } else {
                        painterResource(R.drawable.ic_bookmark_active_light)
                    }

                } else {
                    painterResource(R.drawable.ic_bookmark_light)
                },
                contentDescription = stringResource(R.string.download_image),
                tint = if (isBookmarked) Color.Unspecified else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
