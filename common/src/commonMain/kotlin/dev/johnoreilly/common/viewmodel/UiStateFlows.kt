package dev.johnoreilly.common.viewmodel

import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * Projects the repository's people flows into one UI state. Shared by every client, so the
 * loading/refreshing/error rules live in one place regardless of the ViewModel wrapper used.
 * A failed refresh keeps the cached list; only a failure with nothing cached is an [PersonListUiState.Error].
 */
internal fun PeopleInSpaceRepositoryInterface.personListUiState(): Flow<PersonListUiState> = combine(
    fetchPeopleAsFlow(),
    initialSyncCompleted,
    peopleSyncLoading,
    peopleSyncError,
) { people, initialSyncCompleted, syncLoading, error ->
    when {
        people.isEmpty() && !initialSyncCompleted -> PersonListUiState.Loading
        people.isEmpty() && error != null -> PersonListUiState.Error(error.describe())
        else -> PersonListUiState.Success(
            result = people,
            refreshing = syncLoading && initialSyncCompleted,
        )
    }
}

/** Wraps ISS polling as UI state; collecting starts polling and the last position is retained. */
internal fun PeopleInSpaceRepositoryInterface.issPositionUiState(): Flow<IssPositionUiState> =
    pollISSPosition()
        .map<IssPosition, IssPositionUiState> { IssPositionUiState.Success(it) }
        .onStart { emit(IssPositionUiState.Loading) }

private fun Throwable.describe() = message ?: toString()
