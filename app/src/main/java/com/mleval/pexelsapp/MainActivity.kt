package com.mleval.pexelsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mleval.pexelsapp.navigation.MainScreen
import com.mleval.pexelsapp.navigation.NavGraph
import com.mleval.pexelsapp.presentation.screens.home.HomeScreen
import com.mleval.pexelsapp.presentation.screens.home.HomeScreenState
import com.mleval.pexelsapp.presentation.screens.home.HomeViewModel
import com.mleval.pexelsapp.presentation.ui.theme.PexelsAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            viewModel.state.value is HomeScreenState.Loading
        }
        enableEdgeToEdge()
        setContent {
            PexelsAppTheme {
                MainScreen()

            }
        }
    }
}
