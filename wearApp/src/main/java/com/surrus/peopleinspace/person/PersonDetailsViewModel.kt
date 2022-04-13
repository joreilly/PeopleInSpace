package com.surrus.peopleinspace.person

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.peopleinspace.PERSON_NAME_NAV_ARGUMENT
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PersonDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    peopleInSpaceRepository: PeopleInSpaceRepositoryInterface,
) : ViewModel() {
    private val personName: String = savedStateHandle[PERSON_NAME_NAV_ARGUMENT]!!

    val person = peopleInSpaceRepository.fetchPeopleAsFlow().map { list ->
        list.find { it.name == personName }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
