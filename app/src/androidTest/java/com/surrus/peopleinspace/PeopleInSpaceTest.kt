package com.surrus.peopleinspace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.surrus.peopleinspace.issposition.ISSPositionMapTag
import com.surrus.peopleinspace.issposition.ISSPositionScreen
import com.surrus.peopleinspace.issposition.ISSPositionViewModel
import com.surrus.peopleinspace.issposition.IssPositionKey
import com.surrus.peopleinspace.personlist.PersonListScreen
import com.surrus.peopleinspace.personlist.PersonListTag
import com.surrus.peopleinspace.personlist.PersonListViewModel
import org.junit.Rule
import org.junit.Test

class PeopleInSpaceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val peopleInSpaceRepository = PeopleInSpaceRepositoryFake()

    @Test
    fun testPeopleListScreen() {
        val viewModel = PersonListViewModel(peopleInSpaceRepository)
        composeTestRule.setContent {
            PersonListScreen(personSelected = {},
                viewModel = viewModel
            )
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

    @Test
    fun testISSPositionScreen() {
        val viewModel = ISSPositionViewModel(peopleInSpaceRepository)
        composeTestRule.setContent {
            ISSPositionScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag(ISSPositionMapTag).assertIsDisplayed()

        val expectedIssPosition = peopleInSpaceRepository.issPosition
        composeTestRule
            .onNode(SemanticsMatcher.expectValue(IssPositionKey, expectedIssPosition))
            .assertExists()
    }

}
