@file:OptIn(ExperimentalHorologistApi::class)

package com.surrus.peopleinspace

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.AppScaffold
import com.surrus.peopleinspace.list.PersonListScreen
import com.surrus.peopleinspace.map.IssMapScreen
import com.surrus.peopleinspace.person.PersonDetailsScreen

@Composable
fun PeopleInSpaceApp(navController: NavHostController) {
    AppScaffold {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = Screen.PersonList.route
        ) {
            composable(
                route = Screen.PersonList.route,
                deepLinks = listOf(navDeepLink { this.uriPattern = "${DEEPLINK_URI}personList" }),
            ) {
                PersonListScreen(
                    modifier = Modifier.fillMaxSize(),
                    personSelected = {
                        navController.navigate(Screen.PersonDetails.route + "/${it.name}")
                    },
                    issMapClick = {
                        navController.navigate(Screen.IssMap.route)
                    },
                )
            }

            composable(
                route = Screen.PersonDetails.route + "/{$PERSON_NAME_NAV_ARGUMENT}",
                arguments = listOf(
                    navArgument(PERSON_NAME_NAV_ARGUMENT, builder = {
                        type = NavType.StringType
                    })
                ),
                deepLinks = listOf(navDeepLink {
                    uriPattern = DEEPLINK_URI + "personList/{${PERSON_NAME_NAV_ARGUMENT}}"
                }),
            ) {
                val personName: String =
                    it.arguments!!.getString(PERSON_NAME_NAV_ARGUMENT)!!

                PersonDetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    personName = personName,
                )
            }

            composable(
                route = Screen.IssMap.route,
                deepLinks = listOf(navDeepLink { uriPattern = "${DEEPLINK_URI}issMap" })
            ) {
                IssMapScreen(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}