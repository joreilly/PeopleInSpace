package dev.johnoreilly.peopleinspace

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.johnoreilly.common.viewmodel.PersonListUiState
import dev.johnoreilly.peopleinspace.personlist.PersonListScreen
import dev.johnoreilly.peopleinspace.personlist.PersonListTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Compose UI test run on the JVM with Robolectric (no emulator); the Maestro flow covers the real
// app end to end against the live API. A plain Application keeps the app's Koin start-up out of it;
// SDK 35 because Robolectric's SDK 36 runtime needs Java 21 and CI runs the tests on 17.
@RunWith(RobolectricTestRunner::class)
@Config(application = android.app.Application::class, sdk = [35])
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
