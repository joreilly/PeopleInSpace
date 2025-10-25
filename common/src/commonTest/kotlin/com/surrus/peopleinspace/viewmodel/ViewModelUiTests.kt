package dev.johnoreilly.peopleinspace.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.*
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.viewmodel.PersonListUiState
import dev.johnoreilly.peopleinspace.PeopleInSpaceRepositoryFake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * UI Tests demonstrating state-based testing patterns
 *
 * These tests show how to test Compose UI components that use StateFlow
 * for state management, which is the pattern used by ViewModels in this project.
 *
 * Note: The actual ViewModels use Koin dependency injection and don't accept
 * constructor parameters, so these tests demonstrate testing the UI layer
 * with mock state flows instead of actual ViewModel instances.
 */
@OptIn(ExperimentalTestApi::class)
class ViewModelUiTests {

    private val testDispatcher = StandardTestDispatcher()
    private val repository = PeopleInSpaceRepositoryFake()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testISSPositionDisplay_withStateFlow() = runComposeUiTest {
        // Given - Create a state flow with ISS position data
        val positionFlow = MutableStateFlow(repository.issPosition)

        // When
        setContent {
            MaterialTheme {
                ISSPositionTestContent(positionFlow)
            }
        }

        // Advance time to allow state to update
        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify position data is displayed
        val position = repository.issPosition
        onNodeWithText("ISS Position").assertIsDisplayed()
        onNodeWithText(position.latitude.toString()).assertIsDisplayed()
        onNodeWithText(position.longitude.toString()).assertIsDisplayed()
    }

    @Test
    fun testISSPositionUpdate_whenStateChanges() = runComposeUiTest {
        // Given - Create a mutable state flow
        val positionFlow = MutableStateFlow(IssPosition(0.0, 0.0))

        // When - Set initial content
        setContent {
            MaterialTheme {
                ISSPositionTestContent(positionFlow)
            }
        }

        waitForIdle()

        // Then - Verify initial position
        onNodeWithText("0.0").assertIsDisplayed()

        // When - Update position
        positionFlow.value = repository.issPosition

        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify updated position is displayed
        onNodeWithText(repository.issPosition.latitude.toString()).assertIsDisplayed()
    }

    @Test
    fun testPersonListSuccess_displaysData() = runComposeUiTest {
        // Given - Create state flow with success state
        val uiStateFlow = MutableStateFlow<PersonListUiState>(
            PersonListUiState.Success(repository.peopleList)
        )

        // When
        setContent {
            MaterialTheme {
                PersonListTestContent(uiStateFlow)
            }
        }

        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify people data is displayed
        repository.peopleList.forEach { person ->
            onNodeWithText(person.name).assertIsDisplayed()
            onNodeWithText(person.craft).assertIsDisplayed()
        }
    }

    @Test
    fun testPersonListLoading_displaysLoadingIndicator() = runComposeUiTest {
        // Given - Create state flow with loading state
        val uiStateFlow = MutableStateFlow<PersonListUiState>(PersonListUiState.Loading)

        // When
        setContent {
            MaterialTheme {
                PersonListTestContent(uiStateFlow)
            }
        }

        waitForIdle()

        // Then - Verify loading state is displayed
        onNodeWithText("Loading...").assertIsDisplayed()
    }

    @Test
    fun testPersonListError_displaysError() = runComposeUiTest {
        // Given - Create state flow with error state
        val errorMessage = "Network error"
        val uiStateFlow = MutableStateFlow<PersonListUiState>(
            PersonListUiState.Error(errorMessage)
        )

        // When
        setContent {
            MaterialTheme {
                PersonListTestContent(uiStateFlow)
            }
        }

        waitForIdle()

        // Then - Verify error state is displayed
        onNodeWithText("Error: $errorMessage").assertIsDisplayed()
    }

    @Test
    fun testPersonListDisplaysCorrectCount() = runComposeUiTest {
        // Given
        val expectedCount = repository.peopleList.size
        val uiStateFlow = MutableStateFlow<PersonListUiState>(
            PersonListUiState.Success(repository.peopleList)
        )

        // When
        setContent {
            MaterialTheme {
                PersonListCountTestContent(uiStateFlow)
            }
        }

        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then
        onNodeWithText("People count: $expectedCount").assertIsDisplayed()
    }

    @Test
    fun testPersonListStateTransition_fromLoadingToSuccess() = runComposeUiTest {
        // Given - Start with loading state
        val uiStateFlow = MutableStateFlow<PersonListUiState>(PersonListUiState.Loading)

        // When - Set content
        setContent {
            MaterialTheme {
                PersonListTestContent(uiStateFlow)
            }
        }

        waitForIdle()

        // Then - Verify loading is displayed
        onNodeWithText("Loading...").assertIsDisplayed()

        // When - Transition to success state
        uiStateFlow.value = PersonListUiState.Success(repository.peopleList)

        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify data is now displayed
        onNodeWithText("Loading...").assertDoesNotExist()
        onNodeWithText(repository.peopleList[0].name).assertIsDisplayed()
    }

    // Test composables that accept StateFlow parameters

    @Composable
    private fun ISSPositionTestContent(positionFlow: StateFlow<IssPosition>) {
        val position by positionFlow.collectAsState()

        Column {
            Text("ISS Position")
            Text(position.latitude.toString())
            Text(position.longitude.toString())
        }
    }

    @Composable
    private fun PersonListTestContent(uiStateFlow: StateFlow<PersonListUiState>) {
        val uiState by uiStateFlow.collectAsState()

        Column {
            when (uiState) {
                is PersonListUiState.Success -> {
                    val people = (uiState as PersonListUiState.Success).result
                    people.forEach { person ->
                        Text(person.name)
                        Text(person.craft)
                    }
                }
                is PersonListUiState.Loading -> {
                    Text("Loading...")
                }
                is PersonListUiState.Error -> {
                    val message = (uiState as PersonListUiState.Error).message
                    Text("Error: $message")
                }
            }
        }
    }

    @Composable
    private fun PersonListCountTestContent(uiStateFlow: StateFlow<PersonListUiState>) {
        val uiState by uiStateFlow.collectAsState()

        Column {
            when (uiState) {
                is PersonListUiState.Success -> {
                    val people = (uiState as PersonListUiState.Success).result
                    Text("People count: ${people.size}")
                }
                else -> {
                    Text("Loading or Error")
                }
            }
        }
    }
}
