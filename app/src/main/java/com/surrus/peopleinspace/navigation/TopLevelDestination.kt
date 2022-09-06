package com.surrus.peopleinspace.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
data class TopLevelDestination(
    override val route: String,
    override val destination: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
) : PeopleInSpaceNavigationDestination
