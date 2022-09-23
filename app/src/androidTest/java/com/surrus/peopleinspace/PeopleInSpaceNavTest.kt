package com.surrus.peopleinspace

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.surrus.peopleinspace.issposition.navigation.ISSPositionDestination
import com.surrus.peopleinspace.navigation.PeopleInSpaceNavHost
import com.surrus.peopleinspace.personlist.navigation.PersonListDestination
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    lateinit var navController: TestNavHostController

    @Test
    fun testPeopleInSpaceNavStartDestination() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            PeopleInSpaceNavHost(navController = navController)
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, PersonListDestination.route)

        composeTestRule
            .onNodeWithContentDescription("PeopleInSpace")
            .assertIsDisplayed()
    }


    @Test
    fun testNavigateToISSPositioonScreen() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current)
            navController.navigatorProvider.addNavigator(ComposeNavigator())
            PeopleInSpaceNavHost(navController = navController)

            navController.navigate(ISSPositionDestination.route)
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, ISSPositionDestination.route)

        composeTestRule
            .onNodeWithContentDescription("ISSPosition")
            .assertIsDisplayed()
    }

}
