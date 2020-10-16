package com.surrus.peopleinspace.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PeopleInSpaceViewModel(private val peopleInSpaceRepository: PeopleInSpaceRepository) : ViewModel() {
    val peopleInSpace = MutableStateFlow<List<Assignment>>(emptyList())

    init {
        viewModelScope.launch {
            peopleInSpaceRepository.fetchPeopleAsFlow()?.collect {
                peopleInSpace.value = it
            }
        }
    }

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