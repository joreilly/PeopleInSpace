package com.surrus.peopleinspace.di

import com.surrus.common.viewmodel.ISSPositionViewModel
import com.surrus.common.viewmodel.PersonListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PersonListViewModel)
    viewModelOf(::ISSPositionViewModel)
}
