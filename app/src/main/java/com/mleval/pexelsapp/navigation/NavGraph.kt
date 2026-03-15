package com.mleval.pexelsapp.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mleval.pexelsapp.presentation.screens.details.DetailsScreen
import com.mleval.pexelsapp.presentation.screens.details.Source
import com.mleval.pexelsapp.presentation.screens.home.HomeScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination =  Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onPhotoClick = {
                    navController.navigate(Screen.Details.createRoute(it, Source.HOME))
                }
            )
        }
        composable(Screen.Details.route) {
            DetailsScreen(
                id = Screen.Details.getPhotoId(it.arguments),
                source = Screen.Details.getPhotoSource(it.arguments),
                onNavigationClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Home: Screen("home")

    data object Details: Screen("details/{id}/{source}") {

        fun createRoute(id: Long, source: Source): String {
            return "details/$id/$source"
        }

        fun getPhotoId(arguments: Bundle?): Long {
            return arguments?.getString("id")?.toLong() ?: 0L
        }

        fun getPhotoSource(arguments: Bundle?): Source {
            val source = arguments?.getString("source") ?: Source.HOME.name
            return Source.valueOf(source)
        }
    }

}