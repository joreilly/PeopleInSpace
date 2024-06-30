package com.surrus.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ISSPositionViewModel : ViewModel(), KoinComponent {
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface by inject()

    val position = peopleInSpaceRepository.pollISSPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IssPosition(0.0, 0.0))
}
