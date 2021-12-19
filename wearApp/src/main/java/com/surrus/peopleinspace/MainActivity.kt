package com.surrus.peopleinspace

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavDeepLink
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import coil.ImageLoader
import coil.compose.LocalImageLoader
import org.koin.android.ext.android.inject

sealed class Screen(val route: String) {
    object PersonList : Screen("personList")
    object PersonDetails : Screen("personDetails")
    object IssMap : Screen("issMap")
}

const val PERSON_NAME_NAV_ARGUMENT = "personName"
const val DEEPLINK_URI = "peopleinspace://peopleinspace.dev/"

class MainActivity : ComponentActivity() {
    private val imageLoader: ImageLoader by inject()

    @OptIn(ExperimentalWearMaterialApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CompositionLocalProvider(
                LocalImageLoader provides imageLoader,
            ) {
                val navController = rememberSwipeDismissableNavController()

                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = Screen.PersonList.route
                ) {

                    composable(
                        route = Screen.PersonList.route,
                        deepLinks = listOf(NavDeepLink(DEEPLINK_URI + "personList"))
                    ) {
                        PersonListScreen(
                            personSelected = {
                                navController.navigate(Screen.PersonDetails.route + "/${it.name}")
                            },
                            issMapClick = {
                                navController.navigate(Screen.IssMap.route)
                            })
                    }

                    composable(
                        route = Screen.PersonDetails.route + "/{$PERSON_NAME_NAV_ARGUMENT}",
                        arguments = listOf(
                            navArgument(PERSON_NAME_NAV_ARGUMENT, builder = {
                                this.type = NavType.StringType
                            })),
                        deepLinks = listOf(navDeepLink { uriPattern = DEEPLINK_URI + "personList/{${PERSON_NAME_NAV_ARGUMENT}}" })
                    ) { navBackStackEntry ->
                        val personName = navBackStackEntry.arguments?.getString(PERSON_NAME_NAV_ARGUMENT)
                        personName?.let {
                            PersonDetailsScreen(personName)
                        }
                    }

                    composable(
                        route = Screen.IssMap.route,
                        deepLinks = listOf(NavDeepLink(DEEPLINK_URI + "issMap"))
                    ) {
                        IssMap()
                    }
                }
            }
        }
    }
}
