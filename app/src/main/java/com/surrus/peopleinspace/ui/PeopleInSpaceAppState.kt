package com.surrus.peopleinspace.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.surrus.peopleinspace.R
import com.surrus.peopleinspace.navigation.IssPosition
import com.surrus.peopleinspace.navigation.PersonList
import com.surrus.peopleinspace.navigation.TopLevelDestination


@Composable
fun rememberPeopleInSpaceAppState(
    windowSizeClass: WindowSizeClass
): PeopleInSpaceAppState {
    return remember(windowSizeClass) {
        PeopleInSpaceAppState(windowSizeClass)
    }
}


@Stable
class PeopleInSpaceAppState(
    private val windowSizeClass: WindowSizeClass
) {
    val shouldShowBottomBar: Boolean
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact ||
            windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

    val shouldShowNavRail: Boolean
        get() = !shouldShowBottomBar

    /**
     * Top level destinations to be used in the BottomBar and NavRail
     */
    val topLevelDestinations: List<TopLevelDestination> = listOf(
        TopLevelDestination(
            route = PersonList,
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            iconTextId = R.string.people
        ),
        TopLevelDestination(
            route = IssPosition,
            selectedIcon = Icons.Filled.LocationOn,
            unselectedIcon = Icons.Outlined.LocationOn,
            iconTextId = R.string.iss_position
        ),
    )
}

