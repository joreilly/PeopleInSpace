package com.surrus.peopleinspace.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PersonDetailsViewModel(
    personName: String,
    peopleInSpaceRepository: PeopleInSpaceRepositoryInterface,
) : ViewModel() {
    val person = peopleInSpaceRepository.fetchPeopleAsFlow().map { list ->
        list.find { it.name == personName }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
