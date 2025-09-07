package dev.johnoreilly.peopleinspace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.johnoreilly.common.viewmodel.PersonListUiState
import dev.johnoreilly.peopleinspace.personlist.PersonListScreen
import dev.johnoreilly.peopleinspace.personlist.PersonListTag
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
