package dev.johnoreilly.peopleinspace.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.*
import dev.johnoreilly.common.ui.CoordinateDisplay
import kotlin.test.Test

/**
 * Compose Multiplatform UI Tests
 *
 * These tests use the multiplatform Compose UI testing framework and can run on
 * Android, iOS, Desktop, and other platforms supported by Compose Multiplatform.
 *
 * Key differences from platform-specific tests:
 * - Uses `runComposeUiTest` instead of `createComposeRule()`
 * - Works with kotlin.test instead of JUnit
 * - Can be executed on multiple platforms
 */
@OptIn(ExperimentalTestApi::class)
class ComposeMultiplatformUiTests {

    @Test
    fun testCoordinateDisplay_showsLabelAndValue() = runComposeUiTest {
        // Given
        val label = "Latitude"
        val value = "53.2743394"

        // When
        setContent {
            MaterialTheme {
                CoordinateDisplay(
                    label = label,
                    value = value
                )
            }
        }

        // Then
        onNodeWithText(label).assertIsDisplayed()
        onNodeWithText(value).assertIsDisplayed()
    }

    @Test
    fun testCoordinateDisplay_longitudeDisplay() = runComposeUiTest {
        // Given
        val label = "Longitude"
        val value = "-9.0514163"

        // When
        setContent {
            MaterialTheme {
                CoordinateDisplay(
                    label = label,
                    value = value
                )
            }
        }

        // Then
        onNodeWithText(label).assertExists()
        onNodeWithText(value).assertExists()
    }

    @Test
    fun testCoordinateDisplay_withDifferentValues() = runComposeUiTest {
        // Given
        val testCases = listOf(
            "Latitude" to "0.0",
            "Longitude" to "180.0",
            "Latitude" to "-90.0"
        )

        testCases.forEach { (label, value) ->
            // When
            setContent {
                MaterialTheme {
                    CoordinateDisplay(
                        label = label,
                        value = value
                    )
                }
            }

            // Then
            onNodeWithText(label).assertIsDisplayed()
            onNodeWithText(value).assertIsDisplayed()
        }
    }
}
