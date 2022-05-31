package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.surrus.peopleinspace.issposition.ISSPositionScreen
import com.surrus.peopleinspace.personlist.PersonListScreen
import com.surrus.peopleinspace.persondetails.PersonDetailsScreen
import com.surrus.peopleinspace.ui.PeopleInSpaceTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PeopleInSpaceTheme {
                MainLayout()
            }
        }
    }
}

sealed class Screen(val title: String) {
    object PersonList : Screen("PersonList")
    object PersonDetails : Screen("PersonDetails")
    object ISSPositionScreen : Screen("ISSPosition")
}

data class BottomNavigationitem(
    val route: String,
    val icon: ImageVector,
    val iconContentDescription: String
)

val bottomNavigationItems = listOf(
    BottomNavigationitem(
        Screen.PersonList.title,
        Icons.Default.Person,
        "People"
    ),
    BottomNavigationitem(
        Screen.ISSPositionScreen.title,
        Icons.Filled.LocationOn,
        "ISS Position"
    )
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout() {
    val navController = rememberAnimatedNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                bottomNavigationItems.forEach { bottomNavigationitem ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                bottomNavigationitem.icon,
                                contentDescription = bottomNavigationitem.iconContentDescription
                            )
                        },
                        selected = currentRoute == bottomNavigationitem.route,
                        onClick = {
                            navController.navigate(bottomNavigationitem.route) {
                                popUpTo(navController.graph.id)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->

        AnimatedNavHost(navController, startDestination = Screen.PersonList.title) {
            composable(
                route = Screen.PersonList.title,
                exitTransition = {
                    slideOutHorizontally() +
                    fadeOut(animationSpec = tween(1000))
                },
                popEnterTransition = {
                    slideInHorizontally()
                }
            ) {
                PersonListScreen(
                    paddingValues = paddingValues,
                    personSelected = {
                        navController.navigate(Screen.PersonDetails.title + "/${it.name}")
                    }
                )
            }
            composable(
                route = Screen.PersonDetails.title + "/{person}",
                enterTransition = {
                    slideInHorizontally() +
                    fadeIn(animationSpec = tween(1000))
                },
                popExitTransition = {
                    slideOutHorizontally()
                }
            ) { backStackEntry ->
                PersonDetailsScreen(
                    backStackEntry.arguments?.get("person") as String,
                    popBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ISSPositionScreen.title) {
                ISSPositionScreen()
            }
        }
    }
}


