package com.mleval.pexelsapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mleval.pexelsapp.R

data class BottomNavItem(
    val route: String,
    val iconSelected: Int,
    val iconUnselected: Int
)
@Composable
fun BottomNavigationBar(
    navController: NavController
) {

    val items = listOf(
        BottomNavItem(
            route = "home",
            iconSelected =  if (isSystemInDarkTheme()) R.drawable.ic_selected_home_dark else R.drawable.ic_selected_home_light,
            iconUnselected = if (isSystemInDarkTheme()) R.drawable.ic_home_dark else R.drawable.ic_home_light
        ),
        BottomNavItem(
            route = "bookmark",
            iconSelected =  if (isSystemInDarkTheme()) R.drawable.ic_bookmark_active_dark else R.drawable.ic_bookmark_active_light,
            iconUnselected = if (isSystemInDarkTheme()) R.drawable.ic_bookmark_dark else R.drawable.ic_bookmark_light
        )
    )

    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, item ->
            val selected = currentRoute == item.route
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                navController.navigate(item.route) {
                                    launchSingleTop = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            modifier = Modifier.padding(bottom = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            if (selected) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .height(3.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary
                                        )
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                painter = painterResource(
                                    if (selected) item.iconSelected
                                    else item.iconUnselected
                                ),
                                contentDescription = item.route,
                                tint = Color.Unspecified
                            )
                        }
                    }
                }

        }
}
