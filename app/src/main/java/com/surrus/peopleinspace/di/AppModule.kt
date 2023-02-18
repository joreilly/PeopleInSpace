package com.surrus.peopleinspace.di

import com.surrus.peopleinspace.issposition.ISSPositionViewModel
import com.surrus.peopleinspace.persondetails.PersonDetailsViewModel
import com.surrus.peopleinspace.personlist.PersonListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PersonListViewModel)
    viewModelOf(::PersonDetailsViewModel)
    viewModelOf(::ISSPositionViewModel)
}
