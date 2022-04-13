package com.surrus.peopleinspace.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PersonListViewModel(
    peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {
    val peopleInSpace = peopleInSpaceRepository.fetchPeopleAsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
