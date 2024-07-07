package com.surrus.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


sealed class PersonListUiState {
    object Loading : PersonListUiState()
    data class Error(val message: String) : PersonListUiState()
    data class Success(val result: List<Assignment>) : PersonListUiState()
}


class PersonListViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {

    val uiState = peopleInSpaceRepository.fetchPeopleAsFlow()
        .map { PersonListUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonListUiState.Loading)

    fun refresh() {
        viewModelScope.launch {
            peopleInSpaceRepository.fetchAndStorePeople()
        }
    }
}
