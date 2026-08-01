package dev.johnoreilly.common.di

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal fun viewModelsModule() = module {
    viewModelOf(::PersonListViewModel)
    viewModelOf(::ISSPositionViewModel)
}

// Helpers for native UI clients to resolve lifecycle ViewModels from Koin.
fun personListViewModel(): PersonListViewModel = KoinPlatform.getKoin().get()

fun issPositionViewModel(): ISSPositionViewModel = KoinPlatform.getKoin().get()
