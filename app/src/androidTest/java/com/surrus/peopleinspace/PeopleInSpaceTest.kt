package com.surrus.peopleinspace

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.surrus.peopleinspace.ui.PeopleInSpaceViewModel
import com.surrus.peopleinspace.ui.PersonListScreen
import com.surrus.peopleinspace.ui.PersonListTag
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

        val peopleList = peopleInSpaceRepository.peopleList

        composeTestRule.onNodeWithTag(PersonListTag).assertIsDisplayed()
        composeTestRule.onNodeWithTag(PersonListTag).onChildren().assertCountEquals(peopleList.size)

        peopleList.forEach { person ->
            composeTestRule
                .onNodeWithText(person.name)
                .assertExists()
        }
    }
}
