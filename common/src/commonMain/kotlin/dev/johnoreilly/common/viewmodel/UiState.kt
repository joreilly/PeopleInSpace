package dev.johnoreilly.common.viewmodel

import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.remote.IssPosition

public sealed class PersonListUiState {
    public object Loading : PersonListUiState()
    public data class Error(val message: String) : PersonListUiState()
    public data class Success(
        public val result: List<Assignment>,
        /** True while a later synchronisation runs, so the cached list stays usable. */
        public val refreshing: Boolean = false,
    ) : PersonListUiState()
}

public sealed class IssPositionUiState {
    public object Loading : IssPositionUiState()
    public data class Success(public val position: IssPosition) : IssPositionUiState()
}
