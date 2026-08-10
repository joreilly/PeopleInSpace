package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.remote.Assignment
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel


sealed class PersonListUiState {
    object Loading : PersonListUiState()
    data class Error(val message: String) : PersonListUiState()
    data class Success(val result: List<Assignment>) : PersonListUiState()
}

@KoinViewModel
class PersonListViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {

    val uiState = combine(
        peopleInSpaceRepository.fetchPeopleAsFlow(),
        peopleInSpaceRepository.initialSyncCompleted
    ) { people, initialSyncCompleted ->
        if (people.isEmpty() && !initialSyncCompleted) {
            PersonListUiState.Loading
        } else {
            PersonListUiState.Success(people)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonListUiState.Loading)

    fun refresh() {
        viewModelScope.launch {
            peopleInSpaceRepository.fetchAndStorePeople()
        }
    }
}
