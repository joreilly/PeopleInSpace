package dev.johnoreilly.peopleinspace.di

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::PersonListViewModel)
    viewModelOf(::ISSPositionViewModel)
}
