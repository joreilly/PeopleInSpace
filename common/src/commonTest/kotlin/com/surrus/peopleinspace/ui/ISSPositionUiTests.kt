package dev.johnoreilly.peopleinspace.ui

import androidx.compose.ui.test.*
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.ui.CoordinateDisplay
import dev.johnoreilly.peopleinspace.PeopleInSpaceRepositoryFake
import kotlin.test.Test

/**
 * UI Tests for ISS Position components
 *
 * These tests demonstrate testing Compose Multiplatform UI components
 * with realistic data from a fake repository.
 */
@OptIn(ExperimentalTestApi::class)
class ISSPositionUiTests {

    private val repository = PeopleInSpaceRepositoryFake()

    @Test
    fun testCoordinateDisplay_withISSPosition_displaysLatitude() = runComposeUiTest {
        // Given
        val position = repository.issPosition

        // When
        setContent {
            CoordinateDisplay(
                label = "Latitude",
                value = position.latitude.toString()
            )
        }

        // Then
        onNodeWithText("Latitude").assertIsDisplayed()
        onNodeWithText(position.latitude.toString()).assertIsDisplayed()
    }

    @Test
    fun testCoordinateDisplay_withISSPosition_displaysLongitude() = runComposeUiTest {
        // Given
        val position = repository.issPosition

        // When
        setContent {
            CoordinateDisplay(
                label = "Longitude",
                value = position.longitude.toString()
            )
        }

        // Then
        onNodeWithText("Longitude").assertIsDisplayed()
        onNodeWithText(position.longitude.toString()).assertIsDisplayed()
    }

    @Test
    fun testCoordinateDisplay_withZeroCoordinates() = runComposeUiTest {
        // Given
        val position = IssPosition(0.0, 0.0)

        // When
        setContent {
            CoordinateDisplay(
                label = "Latitude",
                value = position.latitude.toString()
            )
        }

        // Then
        onNodeWithText("0.0").assertExists()
    }

    @Test
    fun testCoordinateDisplay_withNegativeCoordinates() = runComposeUiTest {
        // Given
        val position = IssPosition(-45.5, -120.8)

        // When - Display Latitude
        setContent {
            CoordinateDisplay(
                label = "Latitude",
                value = position.latitude.toString()
            )
        }

        // Then
        onNodeWithText("-45.5").assertIsDisplayed()
    }

    @Test
    fun testCoordinateDisplay_withExtremeCoordinates() = runComposeUiTest {
        // Given - Test North Pole
        val northPole = IssPosition(90.0, 0.0)

        // When
        setContent {
            CoordinateDisplay(
                label = "Latitude",
                value = northPole.latitude.toString()
            )
        }

        // Then
        onNodeWithText("90.0").assertIsDisplayed()

        // Given - Test South Pole
        val southPole = IssPosition(-90.0, 0.0)

        // When
        setContent {
            CoordinateDisplay(
                label = "Latitude",
                value = southPole.latitude.toString()
            )
        }

        // Then
        onNodeWithText("-90.0").assertIsDisplayed()
    }
}
