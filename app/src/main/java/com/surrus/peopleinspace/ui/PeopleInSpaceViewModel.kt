package com.surrus.peopleinspace.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.surrus.common.remote.Assignment
import com.surrus.common.repository.PeopleInSpaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PeopleInSpaceViewModel(peopleInSpaceRepository: PeopleInSpaceRepository) : ViewModel() {
    val peopleInSpace = MutableLiveData<List<Assignment>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val people = peopleInSpaceRepository.fetchPeople()
            peopleInSpace.postValue(people)
        }
    }
}