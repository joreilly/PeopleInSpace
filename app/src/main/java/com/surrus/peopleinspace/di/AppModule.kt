package com.surrus.peopleinspace.di

import com.surrus.common.repository.PeopleInSpaceRepository
import com.surrus.peopleinspace.ui.PeopleInSpaceViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { PeopleInSpaceViewModel(get()) }

    single { PeopleInSpaceRepository() }
}
