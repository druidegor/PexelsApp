package com.mleval.pexelsapp.presentation.screens.home

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {

    Scaffold(

    ) {innerPadding ->
        val state by viewModel.state.collectAsState()

        when(state) {
            HomeScreenState.Error -> TODO()
            is HomeScreenState.HomeContent -> TODO()
            HomeScreenState.Loading -> TODO()
        }
    }
}