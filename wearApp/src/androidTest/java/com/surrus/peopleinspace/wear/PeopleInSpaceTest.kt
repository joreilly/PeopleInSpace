package com.surrus.peopleinspace.wear

import androidx.compose.ui.test.assertContentDescriptionEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.surrus.common.remote.Assignment
import com.surrus.peopleinspace.list.PersonList
import com.surrus.peopleinspace.list.PersonListTag
import org.junit.Rule
import org.junit.Test

class PeopleInSpaceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val peopleList = listOf(
        Assignment(
            "Apollo 11",
            "Neil Armstrong",
            "https://www.biography.com/.image/ar_1:1%2Cc_fill%2Ccs_srgb%2Cfl_progressive%2Cq_auto:good%2Cw_1200/MTc5OTk0MjgyMzk5MTE0MzYy/gettyimages-150832381.jpg"
        ),
        Assignment(
            "Apollo 11",
            "Buzz Aldrin",
            "https://nypost.com/wp-content/uploads/sites/2/2018/06/buzz-aldrin.jpg?quality=80&strip=all"
        )
    )

    @Test
    fun testPeopleListScreenEmpty() {
        composeTestRule.setContent {
            PersonList(
                people = listOf(),
                personSelected = {},
                issMapClick = {},
                columnState = ScalingLazyColumnDefaults.belowTimeText().create()
            )
        }

        composeTestRule.onNodeWithTag("Person").assertDoesNotExist()
    }

    @Test
    fun testPeopleListScreen() {
        composeTestRule.setContent {
            PersonList(
                people = peopleList,
                personSelected = {},
                issMapClick = {},
                columnState = ScalingLazyColumnDefaults.belowTimeText().create()
            )
        }

        val personListNode = composeTestRule.onNodeWithTag(PersonListTag)
        personListNode.assertIsDisplayed()

        val neilNode =
            composeTestRule.onNodeWithText("Neil Armstrong", useUnmergedTree = true).onParent()
        neilNode.assertIsDisplayed()
        neilNode.assertContentDescriptionEquals("Neil Armstrong on Apollo 11")

        val buzzNode =
            composeTestRule.onNodeWithText("Buzz Aldrin", useUnmergedTree = true).onParent()
        buzzNode.assertIsDisplayed()
        buzzNode.assertContentDescriptionEquals("Buzz Aldrin on Apollo 11")
    }
}
