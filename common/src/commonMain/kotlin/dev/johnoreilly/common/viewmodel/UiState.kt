package dev.johnoreilly.common.viewmodel

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition

sealed class PersonListUiState {
    object Loading : PersonListUiState()
    data class Error(val message: String) : PersonListUiState()
    data class Success(
        val result: List<Assignment>,
        /** True while a later synchronisation runs, so the cached list stays usable. */
        val refreshing: Boolean = false,
    ) : PersonListUiState()
}

sealed class IssPositionUiState {
    object Loading : IssPositionUiState()
    data class Success(val position: IssPosition) : IssPositionUiState()
}
