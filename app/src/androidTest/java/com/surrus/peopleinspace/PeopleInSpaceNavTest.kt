package com.surrus.peopleinspace

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import com.surrus.peopleinspace.navigation.IssPosition
import com.surrus.peopleinspace.navigation.PeopleInSpaceNavHost
import com.surrus.peopleinspace.navigation.PersonList
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
        assertEquals(route, PersonList::class.qualifiedName)

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

            navController.navigate(IssPosition)
        }

        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, IssPosition::class.qualifiedName)

        composeTestRule
            .onNodeWithContentDescription("ISSPosition")
            .assertIsDisplayed()
    }

}
