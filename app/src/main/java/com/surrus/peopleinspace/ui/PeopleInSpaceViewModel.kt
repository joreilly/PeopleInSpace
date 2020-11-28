package com.surrus.peopleinspace.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Kermit
import com.surrus.common.remote.Assignment
import com.surrus.common.remote.IssPosition
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class PeopleInSpaceViewModel(
    private val peopleInSpaceRepository: PeopleInSpaceRepository,
    private val logger: Kermit
) : ViewModel() {

    val peopleInSpace = peopleInSpaceRepository.fetchPeopleAsFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val issPosition: LiveData<IssPosition> = peopleInSpaceRepository.pollISSPosition().asLiveData()

    fun getPersonBio(personName: String): String {
        return peopleInSpaceRepository.getPersonBio(personName)
    }

    fun getPersonImage(personName: String): String {
        return peopleInSpaceRepository.getPersonImage(personName)
    }

    fun getPerson(personName: String): Assignment? {
        return peopleInSpace.value.find { it.name == personName}
    }
}