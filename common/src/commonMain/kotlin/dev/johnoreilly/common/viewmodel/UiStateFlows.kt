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

/**
 * Projects the ISS polling flows into one UI state. Collecting this flow starts polling; the
 * last known position is retained across failed polls.
 */
internal fun PeopleInSpaceRepositoryInterface.issPositionUiState(): Flow<IssPositionUiState> = combine(
    pollISSPosition().map<IssPosition, IssPosition?> { it }.onStart { emit(null) },
    issPollLoading,
    issPollError,
) { position, loading, error ->
    when {
        position == null && error != null -> IssPositionUiState.Error(error.describe())
        position == null -> IssPositionUiState.Loading
        else -> IssPositionUiState.Success(position = position, refreshing = loading)
    }
}

private fun Throwable.describe() = message ?: toString()
