package com.surrus.peopleinspace.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MapViewModel(
    peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {
    val issPosition = peopleInSpaceRepository.pollISSPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
