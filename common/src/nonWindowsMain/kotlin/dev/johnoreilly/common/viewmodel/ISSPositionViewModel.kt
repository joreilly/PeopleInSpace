package dev.johnoreilly.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.johnoreilly.common.remote.IssPosition
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ISSPositionViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepositoryInterface
) : ViewModel() {

    val position = peopleInSpaceRepository.pollISSPosition()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IssPosition(0.0, 0.0))
}
