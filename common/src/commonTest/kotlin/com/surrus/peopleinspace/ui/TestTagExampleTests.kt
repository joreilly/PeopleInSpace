package dev.johnoreilly.peopleinspace.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import kotlin.test.Test

/**
 * Example tests demonstrating the use of test tags in Compose Multiplatform
 *
 * Test tags are essential for writing robust UI tests as they provide
 * stable identifiers that don't depend on text content or structure.
 *
 * Best Practices:
 * 1. Use descriptive test tag names (e.g., "PersonList", "RefreshButton")
 * 2. Define test tags as constants to avoid typos
 * 3. Apply test tags to key interactive elements and containers
 * 4. Use test tags over text matching for better test stability
 */
@OptIn(ExperimentalTestApi::class)
class TestTagExampleTests {

    companion object {
        const val TITLE_TAG = "title"
        const val SUBTITLE_TAG = "subtitle"
        const val CONTAINER_TAG = "container"
        const val BUTTON_TAG = "actionButton"
    }

    @Test
    fun testTestTag_findElementByTag() = runComposeUiTest {
        // When
        setContent {
            MaterialTheme {
                Text(
                    text = "Hello, World!",
                    modifier = Modifier.testTag(TITLE_TAG)
                )
            }
        }

        // Then - Find element by test tag
        onNodeWithTag(TITLE_TAG).assertIsDisplayed()
        onNodeWithTag(TITLE_TAG).assertTextEquals("Hello, World!")
    }

    @Test
    fun testTestTag_multipleElements() = runComposeUiTest {
        // When
        setContent {
            MaterialTheme {
                Column(modifier = Modifier.testTag(CONTAINER_TAG)) {
                    Text(
                        text = "Title",
                        modifier = Modifier.testTag(TITLE_TAG)
                    )
                    Text(
                        text = "Subtitle",
                        modifier = Modifier.testTag(SUBTITLE_TAG)
                    )
                }
            }
        }

        // Then - Verify all elements exist
        onNodeWithTag(CONTAINER_TAG).assertExists()
        onNodeWithTag(TITLE_TAG).assertExists()
        onNodeWithTag(SUBTITLE_TAG).assertExists()

        // Verify text content
        onNodeWithTag(TITLE_TAG).assertTextEquals("Title")
        onNodeWithTag(SUBTITLE_TAG).assertTextEquals("Subtitle")
    }

    @Test
    fun testTestTag_verifyHierarchy() = runComposeUiTest {
        // When
        setContent {
            MaterialTheme {
                Column(modifier = Modifier.testTag(CONTAINER_TAG)) {
                    Text("Item 1", modifier = Modifier.testTag("item1"))
                    Text("Item 2", modifier = Modifier.testTag("item2"))
                    Text("Item 3", modifier = Modifier.testTag("item3"))
                }
            }
        }

        // Then - Verify container has children
        onNodeWithTag(CONTAINER_TAG)
            .assertExists()
            .onChildren()
            .assertCountEquals(3)

        // Verify specific children
        onNodeWithTag("item1").assertTextEquals("Item 1")
        onNodeWithTag("item2").assertTextEquals("Item 2")
        onNodeWithTag("item3").assertTextEquals("Item 3")
    }

    @Test
    fun testTestTag_combinedWithTextMatching() = runComposeUiTest {
        // When
        setContent {
            MaterialTheme {
                Column(modifier = Modifier.testTag(CONTAINER_TAG)) {
                    Text("Space Station")
                    Text("ISS Position")
                    Text("Astronauts")
                }
            }
        }

        // Then - Combine test tag with text search
        onNodeWithTag(CONTAINER_TAG)
            .onChildren()
            .assertCountEquals(3)

        // Find specific text within container
        onNode(
            hasTestTag(CONTAINER_TAG) and hasText("ISS Position", substring = true)
        ).assertExists()
    }

    @Test
    fun testTestTag_notFound() = runComposeUiTest {
        // When
        setContent {
            MaterialTheme {
                Text("Hello", modifier = Modifier.testTag("existing"))
            }
        }

        // Then - Existing tag is found
        onNodeWithTag("existing").assertExists()

        // Non-existing tag is not found
        onNodeWithTag("nonExisting").assertDoesNotExist()
    }
}
