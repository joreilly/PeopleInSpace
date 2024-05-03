package com.surrus.peopleinspace.ui


import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.surrus.peopleinspace.navigation.PeopleInSpaceNavHost
import com.surrus.peopleinspace.navigation.TopLevelDestination
import com.surrus.peopleinspace.ui.component.PeopleInSpaceBackground

@Composable
fun PeopleInSpaceApp(
    windowSizeClass: WindowSizeClass,
    appState: PeopleInSpaceAppState = rememberPeopleInSpaceAppState(windowSizeClass)
) {
    val navController = rememberNavController()

    PeopleInSpaceTheme {
        PeopleInSpaceBackground {
            Scaffold(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
                contentWindowInsets = WindowInsets(0, 0, 0, 0),
                bottomBar = {
                    if (appState.shouldShowBottomBar) {
                        PeopleInSpaceBottomBar(
                            navController = navController,
                            destinations = appState.topLevelDestinations
                        )
                    }
                }
            ) { padding ->
                Row(
                    Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.safeDrawing.only(
                                WindowInsetsSides.Horizontal
                            )
                        )
                ) {
                    if (appState.shouldShowNavRail) {
                        PeopleInSpaceNavRail(
                            navController = navController,
                            destinations = appState.topLevelDestinations,
                            modifier = Modifier.safeDrawingPadding()
                        )
                    }

                    PeopleInSpaceNavHost(
                        navController = navController,
                        onBackClick = {  navController.popBackStack() },
                        modifier = Modifier
                            .padding(padding)
                            .windowInsetsPadding(WindowInsets.statusBars)
                    )
                }
            }
        }
    }
}


@Composable
private fun PeopleInSpaceNavRail(
    navController: NavController,
    destinations: List<TopLevelDestination>,
    modifier: Modifier = Modifier,
) {

    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = PeopleInSpaceNavigationDefaults.navigationContentColor(),
    ) {
        destinations.forEach { destination ->
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination
            val selected = currentDestination?.route == destination.route::class.qualifiedName
            NavigationRailItem(
                selected = selected,
                onClick = { navController.navigate(destination.route) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    Icon(icon, contentDescription = stringResource(destination.iconTextId))
                }
            )
        }
    }
}


@Composable
private fun PeopleInSpaceBottomBar(
    navController: NavController,
    destinations: List<TopLevelDestination>
) {
    NavigationBar(
        contentColor = PeopleInSpaceNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
    ) {
        destinations.forEach { destination ->
            val currentDestination = navController.currentBackStackEntryAsState().value?.destination
            val selected = currentDestination?.route == destination.route::class.qualifiedName
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(destination.route) },
                icon = {
                    val icon = if (selected) {
                        destination.selectedIcon
                    } else {
                        destination.unselectedIcon
                    }
                    Icon(icon, contentDescription = stringResource(destination.iconTextId))
                },
                label = { Text(stringResource(destination.iconTextId)) }
            )
        }
    }
}

object PeopleInSpaceNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant
}

