package com.surrus.peopleinspace

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.surrus.peopleinspace.ui.PeopleInSpaceViewModel
import com.surrus.peopleinspace.ui.PersonListScreen
import org.junit.Rule
import org.junit.Test

class PeopleInSpaceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val peopleInSpaceRepository = PeopleInSpaceRepositoryFake()
    private val peopleInSpaceViewModel = PeopleInSpaceViewModel(peopleInSpaceRepository)


    @Test
    fun testGetPeople() {
        composeTestRule.setContent {
            PersonListScreen(
                paddingValues = PaddingValues(),
                personSelected = {},
                peopleInSpaceViewModel = peopleInSpaceViewModel
            )
        }
        
        composeTestRule
            .onNodeWithText("Megan McArthur")
            .assertIsDisplayed()
    }
}
