package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ISSPositionViewModel : ViewModel(), KoinComponent {
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface by inject()

    val position = peopleInSpaceRepository.pollISSPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IssPosition(0.0, 0.0))
}
