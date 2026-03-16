package com.mleval.pexelsapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mleval.pexelsapp.presentation.components.BottomNavigationBar

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar =
        currentRoute == Screen.Home.route ||
                currentRoute == Screen.Bookmark.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->

        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}