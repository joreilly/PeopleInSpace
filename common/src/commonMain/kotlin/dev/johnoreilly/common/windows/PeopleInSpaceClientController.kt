package dev.johnoreilly.common.windows

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Projects a repository into the small Windows-facing API. This is intentionally
 * independent of the Windows HTTP/SQLite setup so state transitions can be
 * verified with a fake repository.
 */
internal class PeopleInSpaceClientController(
    private val repository: PeopleInSpaceRepositoryInterface,
    private val scope: CoroutineScope,
    private val closeResources: () -> Unit = {},
) {
    private val lastPosition = MutableStateFlow<IssPosition?>(null)
    private var closed = false

    val peopleState: StateFlow<PeopleState> = combine(
        repository.fetchPeopleAsFlow(),
        repository.peopleSyncLoading,
        repository.peopleSyncError,
        repository.initialSyncCompleted,
    ) { people, syncLoading, error, initialSyncCompleted ->
        PeopleState(
            people = people.map(Assignment::toPerson),
            initialLoading = !initialSyncCompleted,
            refreshing = syncLoading && initialSyncCompleted,
            errorMessage = error?.message ?: error?.toString(),
        )
    }.stateIn(scope, SharingStarted.Eagerly, PeopleState())

    val issState: StateFlow<IssState> = combine(
        lastPosition,
        repository.issPollLoading,
        repository.issPollError,
    ) { position, loading, error ->
        IssState(
            latitude = position?.latitude ?: 0.0,
            longitude = position?.longitude ?: 0.0,
            timestamp = position?.timestamp ?: 0,
            hasPosition = position != null,
            loading = loading,
            errorMessage = error?.message ?: error?.toString(),
        )
    }.stateIn(scope, SharingStarted.Eagerly, IssState())

    init {
        scope.launch {
            repository.pollISSPosition().collect { position ->
                lastPosition.value = position
            }
        }
    }

    suspend fun refresh() {
        repository.fetchAndStorePeople()
    }

    fun close() {
        if (closed) return
        closed = true
        scope.cancel()
        closeResources()
    }
}

private fun Assignment.toPerson() = Person(
    name = name,
    craft = craft,
    personImageUrl = personImageUrl,
    personBio = personBio,
    nationality = nationality,
)
