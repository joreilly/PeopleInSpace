package com.surrus.peopleinspace.personlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class PersonListUiState(
    val items: List<Assignment> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null
)

class PersonListViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {

    val uiState = peopleInSpaceRepository.fetchPeopleAsFlow()
        .map { PersonListUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PersonListUiState(isLoading = true))

    fun refresh() {
        viewModelScope.launch {
            peopleInSpaceRepository.fetchAndStorePeople()
        }
    }
}
