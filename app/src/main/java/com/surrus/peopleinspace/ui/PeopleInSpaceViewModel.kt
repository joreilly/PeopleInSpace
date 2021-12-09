package com.surrus.peopleinspace.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PeopleInSpaceViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {

    val peopleInSpace = peopleInSpaceRepository.fetchPeopleAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val issPosition = peopleInSpaceRepository.pollISSPosition()

    fun getPerson(personName: String): Assignment? {
        return peopleInSpace.value.find { it.name == personName }
    }
}
