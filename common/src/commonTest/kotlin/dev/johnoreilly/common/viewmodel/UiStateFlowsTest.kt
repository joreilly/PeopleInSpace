package dev.johnoreilly.common.viewmodel

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.remote.OrbitPoint
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class UiStateFlowsTest {
    @Test
    fun peopleStateIsLoadingUntilTheRepositoryCompletesItsFirstSync() = runTest {
        val repository = FakeRepository()
        val state = peopleState(repository)

        runCurrent()

        assertIs<PersonListUiState.Loading>(state.value)
    }

    @Test
    fun peopleStateReportsARefreshInProgressThenTheNewData() = runTest {
        val refreshStarted = CompletableDeferred<Unit>()
        val releaseRefresh = CompletableDeferred<Unit>()
        val repository = FakeRepository().apply {
            initialSyncCompletedMutable.value = true
            onRefresh = {
                peopleSyncLoadingMutable.value = true
                refreshStarted.complete(Unit)
                releaseRefresh.await()
                peopleMutable.value = listOf(Assignment(name = "Mae Jemison", craft = "ISS"))
                peopleSyncLoadingMutable.value = false
            }
        }
        val state = peopleState(repository)

        val refreshJob = launch { repository.fetchAndStorePeople() }
        refreshStarted.await()
        runCurrent()

        val refreshing = assertIs<PersonListUiState.Success>(state.value)
        assertTrue(refreshing.refreshing)

        releaseRefresh.complete(Unit)
        refreshJob.join()
        runCurrent()

        val done = assertIs<PersonListUiState.Success>(state.value)
        assertFalse(done.refreshing)
        assertEquals("Mae Jemison", done.result.single().name)
    }

    @Test
    fun peopleStateIsAnErrorWhenTheFirstSyncFailsWithNothingCached() = runTest {
        val repository = FakeRepository().apply {
            peopleSyncErrorMutable.value = IllegalStateException("offline")
            initialSyncCompletedMutable.value = true
        }
        val state = peopleState(repository)
        runCurrent()

        assertEquals(PersonListUiState.Error("offline"), state.value)
    }

    @Test
    fun peopleStateRetainsCachedPeopleWhenARefreshFails() = runTest {
        val repository = FakeRepository().apply {
            peopleMutable.value = listOf(Assignment(name = "Sally Ride", craft = "ISS"))
            initialSyncCompletedMutable.value = true
            onRefresh = {
                peopleSyncLoadingMutable.value = true
                peopleSyncErrorMutable.value = IllegalStateException("offline")
                peopleSyncLoadingMutable.value = false
            }
        }
        val state = peopleState(repository)

        repository.fetchAndStorePeople()
        runCurrent()

        val success = assertIs<PersonListUiState.Success>(state.value)
        assertEquals("Sally Ride", success.result.single().name)
        assertFalse(success.refreshing)
    }

    @Test
    fun issStateIsLoadingUntilTheFirstPollThenFollowsEachPosition() = runTest {
        val repository = FakeRepository()
        val state = issState(repository)
        runCurrent()
        assertIs<IssPositionUiState.Loading>(state.value)

        repository.issPositions.emit(IssPosition(latitude = 1.5, longitude = 2.5))
        runCurrent()
        assertEquals(IssPositionUiState.Success(IssPosition(1.5, 2.5)), state.value)

        repository.issPositions.emit(IssPosition(latitude = 3.5, longitude = 4.5))
        runCurrent()
        assertEquals(IssPositionUiState.Success(IssPosition(3.5, 4.5)), state.value)
    }

    private fun TestScope.peopleState(repository: FakeRepository) =
        repository.personListUiState().stateIn(backgroundScope, SharingStarted.Eagerly, PersonListUiState.Loading)

    private fun TestScope.issState(repository: FakeRepository) =
        repository.issPositionUiState().stateIn(backgroundScope, SharingStarted.Eagerly, IssPositionUiState.Loading)
}

private class FakeRepository : PeopleInSpaceRepositoryInterface {
    val initialSyncCompletedMutable = MutableStateFlow(false)
    override val initialSyncCompleted: StateFlow<Boolean> = initialSyncCompletedMutable

    val peopleSyncLoadingMutable = MutableStateFlow(false)
    override val peopleSyncLoading: StateFlow<Boolean> = peopleSyncLoadingMutable

    val peopleSyncErrorMutable = MutableStateFlow<Throwable?>(null)
    override val peopleSyncError: StateFlow<Throwable?> = peopleSyncErrorMutable

    val peopleMutable = MutableStateFlow(emptyList<Assignment>())
    val issPositions = MutableSharedFlow<IssPosition>()
    var onRefresh: suspend () -> Unit = {}

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> = peopleMutable

    override fun pollISSPosition(): Flow<IssPosition> = issPositions

    override suspend fun fetchISSFuturePosition(): List<OrbitPoint> = emptyList()

    override suspend fun fetchAndStorePeople() = onRefresh()
}
