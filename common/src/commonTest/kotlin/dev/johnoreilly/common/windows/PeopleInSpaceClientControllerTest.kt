package dev.johnoreilly.common.windows

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.remote.OrbitPoint
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PeopleInSpaceClientControllerTest {
    @Test
    fun initialStateIsLoadingUntilTheRepositoryCompletesItsFirstSync() = runTest {
        val repository = FakeRepository()
        val controller = PeopleInSpaceClientController(repository, CoroutineScope(backgroundScope.coroutineContext))

        runCurrent()

        assertTrue(controller.peopleState.value.initialLoading)
        assertFalse(controller.peopleState.value.refreshing)
        assertTrue(controller.peopleState.value.people.isEmpty())
        controller.close()
    }

    @Test
    fun refreshProjectsSuccessfulPeopleData() = runTest {
        val refreshStarted = CompletableDeferred<Unit>()
        val releaseRefresh = CompletableDeferred<Unit>()
        val repository = FakeRepository().apply {
            initialSyncCompletedMutable.value = true
            onRefresh = {
                peopleSyncLoadingMutable.value = true
                refreshStarted.complete(Unit)
                releaseRefresh.await()
                initialSyncCompletedMutable.value = true
                peopleMutable.value = listOf(Assignment(name = "Mae Jemison", craft = "ISS"))
                peopleSyncLoadingMutable.value = false
            }
        }
        val controller = PeopleInSpaceClientController(repository, CoroutineScope(backgroundScope.coroutineContext))

        val refreshJob = launch { controller.refresh() }
        refreshStarted.await()
        runCurrent()

        assertFalse(controller.peopleState.value.initialLoading)
        assertTrue(controller.peopleState.value.refreshing)

        releaseRefresh.complete(Unit)
        refreshJob.join()
        runCurrent()

        assertFalse(controller.peopleState.value.initialLoading)
        assertFalse(controller.peopleState.value.refreshing)
        assertEquals("Mae Jemison", controller.peopleState.value.people.single().name)
        assertNull(controller.peopleState.value.errorMessage)
        controller.close()
    }

    @Test
    fun peopleProjectionTurnsEscapedLineBreaksInBiographiesIntoRealOnes() = runTest {
        val repository = FakeRepository().apply {
            initialSyncCompletedMutable.value = true
            peopleMutable.value = listOf(
                Assignment(name = "Andrei Fedyaev", craft = "ISS", personBio = "Cosmonaut. \\r\\n\\r\\nFirst flight."),
            )
        }
        val controller = PeopleInSpaceClientController(repository, CoroutineScope(backgroundScope.coroutineContext))
        runCurrent()

        assertEquals("Cosmonaut. \n\nFirst flight.", controller.peopleState.value.people.single().personBio)
        controller.close()
    }

    @Test
    fun refreshFailureRetainsCachedPeopleAndProjectsTheError() = runTest {
        val repository = FakeRepository().apply {
            peopleMutable.value = listOf(Assignment(name = "Sally Ride", craft = "ISS"))
            initialSyncCompletedMutable.value = true
            onRefresh = {
                peopleSyncLoadingMutable.value = true
                peopleSyncErrorMutable.value = IllegalStateException("offline")
                peopleSyncLoadingMutable.value = false
            }
        }
        val controller = PeopleInSpaceClientController(repository, CoroutineScope(backgroundScope.coroutineContext))

        controller.refresh()
        runCurrent()

        assertEquals("Sally Ride", controller.peopleState.value.people.single().name)
        assertEquals("offline", controller.peopleState.value.errorMessage)
        assertFalse(controller.peopleState.value.initialLoading)
        assertFalse(controller.peopleState.value.refreshing)
        controller.close()
    }

    @Test
    fun issStateProjectsAnErrorThenTheNextSuccessfulPoll() = runTest {
        val repository = FakeRepository()
        val controller = PeopleInSpaceClientController(repository, CoroutineScope(backgroundScope.coroutineContext))
        runCurrent() // subscribe the controller's owned polling collector

        repository.issPollLoadingMutable.value = true
        repository.issPositions.emit(IssPosition(latitude = 1.5, longitude = 2.5, timestamp = 10))
        repository.issPollLoadingMutable.value = false
        runCurrent()
        assertTrue(controller.issState.value.hasPosition)
        assertEquals(1.5, controller.issState.value.latitude)
        assertEquals(Instant.fromEpochSeconds(10), controller.issState.value.timestamp)

        repository.issPollErrorMutable.value = IllegalStateException("temporary failure")
        runCurrent()
        assertEquals("temporary failure", controller.issState.value.errorMessage)

        // A retry clears the old error and replaces the last known position.
        repository.issPollLoadingMutable.value = true
        repository.issPollErrorMutable.value = null
        repository.issPositions.emit(IssPosition(latitude = 3.5, longitude = 4.5, timestamp = 20))
        repository.issPollLoadingMutable.value = false
        runCurrent()
        assertEquals(3.5, controller.issState.value.latitude)
        assertEquals(4.5, controller.issState.value.longitude)
        assertNull(controller.issState.value.errorMessage)
        controller.close()
    }

    @Test
    fun closeIsIdempotentAndReleasesResourcesOnce() = runTest {
        val repository = FakeRepository()
        var closeCount = 0
        val controller = PeopleInSpaceClientController(
            repository = repository,
            scope = CoroutineScope(backgroundScope.coroutineContext),
            closeResources = { closeCount++ },
        )

        controller.close()
        controller.close()

        assertEquals(1, closeCount)
    }
}

private class FakeRepository : PeopleInSpaceRepositoryInterface {
    val initialSyncCompletedMutable = MutableStateFlow(false)
    override val initialSyncCompleted: StateFlow<Boolean> = initialSyncCompletedMutable

    val peopleSyncLoadingMutable = MutableStateFlow(false)
    override val peopleSyncLoading: StateFlow<Boolean> = peopleSyncLoadingMutable

    val peopleSyncErrorMutable = MutableStateFlow<Throwable?>(null)
    override val peopleSyncError: StateFlow<Throwable?> = peopleSyncErrorMutable

    val issPollLoadingMutable = MutableStateFlow(false)
    override val issPollLoading: StateFlow<Boolean> = issPollLoadingMutable

    val issPollErrorMutable = MutableStateFlow<Throwable?>(null)
    override val issPollError: StateFlow<Throwable?> = issPollErrorMutable

    val peopleMutable = MutableStateFlow(emptyList<Assignment>())
    val issPositions = MutableSharedFlow<IssPosition>()
    var onRefresh: suspend () -> Unit = {}

    override fun fetchPeopleAsFlow(): Flow<List<Assignment>> = peopleMutable

    override fun pollISSPosition(): Flow<IssPosition> = issPositions

    override suspend fun fetchISSFuturePosition(): List<OrbitPoint> = emptyList()

    override suspend fun fetchAndStorePeople() = onRefresh()
}
