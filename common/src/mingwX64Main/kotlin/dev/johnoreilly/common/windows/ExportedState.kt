package dev.johnoreilly.common.windows

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.viewmodel.IssPositionUiState
import dev.johnoreilly.common.viewmodel.PersonListUiState

// Flat envelopes for the NuGet export. kotlin-native-nuget 0.3.0 cannot export the shared sealed
// UI state types (xxfast/kotlin-native-nuget#38, #39, #40), so the Windows client hands .NET a
// top-level projection of them instead. Delete these and export PersonListUiState /
// IssPositionUiState directly once the generator handles nested classes.

/** Flat projection of [PersonListUiState]. */
data class PeopleState(
    val people: List<Assignment>,
    val initialLoading: Boolean,
    val refreshing: Boolean,
    val errorMessage: String?,
)

/** Flat projection of [IssPositionUiState]; [position] is only meaningful when [hasPosition]. */
data class IssState(
    val position: IssPosition,
    val hasPosition: Boolean,
)

internal fun PersonListUiState.toExported(): PeopleState = when (this) {
    PersonListUiState.Loading ->
        PeopleState(people = emptyList(), initialLoading = true, refreshing = false, errorMessage = null)
    is PersonListUiState.Error ->
        PeopleState(people = emptyList(), initialLoading = false, refreshing = false, errorMessage = message)
    is PersonListUiState.Success ->
        PeopleState(people = result, initialLoading = false, refreshing = refreshing, errorMessage = null)
}

internal fun IssPositionUiState.toExported(): IssState = when (this) {
    IssPositionUiState.Loading -> IssState(position = NoPosition, hasPosition = false)
    is IssPositionUiState.Success -> IssState(position = position, hasPosition = true)
}

private val NoPosition = IssPosition(latitude = 0.0, longitude = 0.0)
