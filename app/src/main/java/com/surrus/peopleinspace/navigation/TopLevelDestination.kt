package com.surrus.peopleinspace.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val iconTextId: Int
)
