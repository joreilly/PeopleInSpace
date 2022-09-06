package com.surrus.peopleinspace.issposition.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.surrus.peopleinspace.issposition.ISSPositionRoute
import com.surrus.peopleinspace.navigation.PeopleInSpaceNavigationDestination


object ISSPositionDestination : PeopleInSpaceNavigationDestination {
    override val route = "iss_position_route"
    override val destination = "iss_position_destination"
}

fun NavGraphBuilder.issPositionGraph() {
    composable(route = ISSPositionDestination.route) {
        ISSPositionRoute()
    }
}
