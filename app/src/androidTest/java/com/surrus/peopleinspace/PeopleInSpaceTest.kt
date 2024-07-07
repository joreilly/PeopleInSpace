package com.surrus.peopleinspace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.surrus.common.viewmodel.PersonListUiState
import com.surrus.peopleinspace.personlist.PersonListScreen
import com.surrus.peopleinspace.personlist.PersonListTag
import org.junit.Rule
import org.junit.Test

class PeopleInSpaceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val peopleInSpaceRepository = PeopleInSpaceRepositoryFake()

    @Test
    fun testPeopleListScreen() {
        composeTestRule.setContent {
            PersonListScreen(uiState = PersonListUiState.Success(peopleInSpaceRepository.peopleList), navigateToPerson = {}, onRefresh = {})
        }

        val peopleList = peopleInSpaceRepository.peopleList
        val personListNode = composeTestRule.onNodeWithTag(PersonListTag)
        personListNode.assertIsDisplayed()
            .onChildren().assertCountEquals(peopleList.size)

        peopleList.forEachIndexed { index, person ->
            val rowNode = personListNode.onChildAt(index)
            rowNode.assertTextContains(person.name)
            rowNode.assertTextContains(person.craft)
        }
    }

}
