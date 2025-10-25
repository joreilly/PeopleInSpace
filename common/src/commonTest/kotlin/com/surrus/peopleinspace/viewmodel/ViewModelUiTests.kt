package dev.johnoreilly.peopleinspace.viewmodel

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.*
import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import dev.johnoreilly.peopleinspace.PeopleInSpaceRepositoryFake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * UI Tests demonstrating integration with ViewModels
 *
 * These tests show how to test Compose UI components that interact with ViewModels,
 * using a fake repository to provide test data.
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
    fun testISSPositionDisplay_withViewModel() = runComposeUiTest {
        // Given
        val viewModel = ISSPositionViewModel(repository)

        // When
        setContent {
            MaterialTheme {
                ISSPositionTestContent(viewModel)
            }
        }

        // Advance time to allow state to update
        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify position data is displayed
        val position = repository.issPosition
        onNodeWithText(position.latitude.toString()).assertIsDisplayed()
        onNodeWithText(position.longitude.toString()).assertIsDisplayed()
    }

    @Test
    fun testPersonListData_fromViewModel() = runComposeUiTest {
        // Given
        val viewModel = PersonListViewModel(repository)

        // When
        setContent {
            MaterialTheme {
                PersonListTestContent(viewModel)
            }
        }

        // Advance time to allow state to update
        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then - Verify people data is accessible
        val people = repository.peopleList
        people.forEach { person ->
            onNodeWithText(person.name).assertIsDisplayed()
        }
    }

    @Test
    fun testPersonListDisplaysCorrectCount() = runComposeUiTest {
        // Given
        val viewModel = PersonListViewModel(repository)
        val expectedCount = repository.peopleList.size

        // When
        setContent {
            MaterialTheme {
                PersonListCountTestContent(viewModel, expectedCount)
            }
        }

        // Advance time
        testDispatcher.scheduler.advanceUntilIdle()
        waitForIdle()

        // Then
        onNodeWithText("People count: $expectedCount").assertIsDisplayed()
    }

    // Test composables for ViewModel integration

    @Composable
    private fun ISSPositionTestContent(viewModel: ISSPositionViewModel) {
        val position by viewModel.position.collectAsState()

        Column {
            Text("ISS Position")
            Text(position.latitude.toString())
            Text(position.longitude.toString())
        }
    }

    @Composable
    private fun PersonListTestContent(viewModel: PersonListViewModel) {
        val uiState by viewModel.uiState.collectAsState()

        Column {
            when (uiState) {
                is dev.johnoreilly.common.viewmodel.PersonListUiState.Success -> {
                    val people = (uiState as dev.johnoreilly.common.viewmodel.PersonListUiState.Success).result
                    people.forEach { person ->
                        Text(person.name)
                        Text(person.craft)
                    }
                }
                is dev.johnoreilly.common.viewmodel.PersonListUiState.Loading -> {
                    Text("Loading...")
                }
                is dev.johnoreilly.common.viewmodel.PersonListUiState.Error -> {
                    Text("Error")
                }
            }
        }
    }

    @Composable
    private fun PersonListCountTestContent(viewModel: PersonListViewModel, expectedCount: Int) {
        val uiState by viewModel.uiState.collectAsState()

        Column {
            when (uiState) {
                is dev.johnoreilly.common.viewmodel.PersonListUiState.Success -> {
                    val people = (uiState as dev.johnoreilly.common.viewmodel.PersonListUiState.Success).result
                    Text("People count: ${people.size}")
                }
                else -> {
                    Text("Loading or Error")
                }
            }
        }
    }
}
