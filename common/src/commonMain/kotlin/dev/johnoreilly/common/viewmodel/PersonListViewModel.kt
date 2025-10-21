package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


sealed class PersonListUiState {
    object Loading : PersonListUiState()
    data class Error(val message: String) : PersonListUiState()
    data class Success(val result: List<Assignment>) : PersonListUiState()
}

class PersonListViewModel() : ViewModel(), KoinComponent {
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface by inject()

    val uiState = peopleInSpaceRepository.fetchPeopleAsFlow()
        .map { PersonListUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonListUiState.Loading)

    fun refresh() {
        viewModelScope.launch {
            peopleInSpaceRepository.fetchAndStorePeople()
        }
    }
}
