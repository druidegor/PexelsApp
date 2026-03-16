package com.mleval.pexelsapp.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mleval.pexelsapp.presentation.screens.bookmarks.BookmarksScreen
import com.mleval.pexelsapp.presentation.screens.details.DetailsScreen
import com.mleval.pexelsapp.presentation.screens.details.Source
import com.mleval.pexelsapp.presentation.screens.home.HomeScreen


@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {

    NavHost(
        navController = navController,
        startDestination =  Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                modifier = modifier,
                onPhotoClick = {
                    navController.navigate(Screen.Details.createRoute(it, Source.HOME)) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Details.route) {
            DetailsScreen(
                id = Screen.Details.getPhotoId(it.arguments),
                source = Screen.Details.getPhotoSource(it.arguments),
                onNavigationClick = {
                    val canGoBack = navController
                        .currentBackStackEntry
                        ?.lifecycle
                        ?.currentState == Lifecycle.State.RESUMED

                    if (canGoBack) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Screen.Bookmark.route) {
            BookmarksScreen(
                modifier = modifier,
                onClick = {
                    val canGoBack = navController
                        .currentBackStackEntry
                        ?.lifecycle
                        ?.currentState == Lifecycle.State.RESUMED

                    if (canGoBack) {
                        navController.popBackStack()
                    }
                },
                onPhotoClick = {
                    navController.navigate(Screen.Details.createRoute(it, Source.BOOKMARKS)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Home: Screen("home")

    data object Bookmark: Screen("bookmark")

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