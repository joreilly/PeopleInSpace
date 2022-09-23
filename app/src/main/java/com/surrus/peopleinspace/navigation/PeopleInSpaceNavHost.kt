package com.surrus.peopleinspace.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.surrus.peopleinspace.issposition.navigation.issPositionGraph
import com.surrus.peopleinspace.persondetails.navigation.PersonDetailsDestination
import com.surrus.peopleinspace.persondetails.navigation.personDetailsGraph
import com.surrus.peopleinspace.personlist.navigation.PersonListDestination
import com.surrus.peopleinspace.personlist.navigation.personListGraph

@Composable
fun PeopleInSpaceNavHost(
    navController: NavHostController,
    onNavigateToDestination: (PeopleInSpaceNavigationDestination, String) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    startDestination: String = PersonListDestination.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        personListGraph(navigateToPerson = {
            onNavigateToDestination(
                PersonDetailsDestination,
                PersonDetailsDestination.createNavigationRoute(it)
            )
        })
        personDetailsGraph(onBackClick = onBackClick)
        issPositionGraph()
    }
}
