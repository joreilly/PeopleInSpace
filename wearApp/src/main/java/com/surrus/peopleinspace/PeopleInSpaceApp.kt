package com.surrus.peopleinspace

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDeepLink
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import androidx.wear.compose.material.ScalingLazyListState
import com.google.android.horologist.compose.navscaffold.NavScaffoldViewModel
import com.google.android.horologist.compose.navscaffold.WearNavScaffold
import com.google.android.horologist.compose.navscaffold.scalingLazyColumnComposable
import com.google.android.horologist.compose.navscaffold.wearNavComposable
import com.surrus.peopleinspace.list.PersonListScreen
import com.surrus.peopleinspace.map.IssMapScreen
import com.surrus.peopleinspace.person.PersonDetailsScreen

@Composable
fun PeopleInSpaceApp(navController: NavHostController) {
    WearNavScaffold(
        navController = navController,
        startDestination = Screen.PersonList.route
    ) {
        scalingLazyColumnComposable(
            route = Screen.PersonList.route,
            deepLinks = listOf(NavDeepLink(DEEPLINK_URI + "personList")),
            scrollStateBuilder = { ScalingLazyListState() }
        ) {
            PersonListScreen(
                modifier = Modifier.fillMaxSize(),
                personSelected = {
                    navController.navigate(Screen.PersonDetails.route + "/${it.name}")
                },
                issMapClick = {
                    navController.navigate(Screen.IssMap.route)
                },
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester,
            )
        }

        scalingLazyColumnComposable(
            route = Screen.PersonDetails.route + "/{$PERSON_NAME_NAV_ARGUMENT}",
            arguments = listOf(
                navArgument(PERSON_NAME_NAV_ARGUMENT, builder = {
                    type = NavType.StringType
                })
            ),
            deepLinks = listOf(navDeepLink {
                uriPattern = DEEPLINK_URI + "personList/{${PERSON_NAME_NAV_ARGUMENT}}"
            }),
            scrollStateBuilder = { ScalingLazyListState(initialCenterItemIndex = 0) }
        ) {
            val personName: String =
                it.backStackEntry.arguments!!.getString(PERSON_NAME_NAV_ARGUMENT)!!

            PersonDetailsScreen(
                modifier = Modifier.fillMaxSize(),
                personName = personName,
                scrollState = it.scrollableState,
                focusRequester = it.viewModel.focusRequester
            )
        }

        wearNavComposable(
            route = Screen.IssMap.route,
            deepLinks = listOf(NavDeepLink(DEEPLINK_URI + "issMap"))
        ) { _, viewModel ->
            viewModel.timeTextMode = NavScaffoldViewModel.TimeTextMode.Off

            IssMapScreen(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}