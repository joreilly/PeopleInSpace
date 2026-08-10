package dev.johnoreilly.common.di

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import org.koin.mp.KoinPlatform

// The view models themselves are picked up by CommonModule's @ComponentScan, which only sees them
// on targets that have this source set. MinGW has no AndroidX lifecycle, so it contributes none.

// Helpers for native UI clients to resolve lifecycle ViewModels from Koin.
fun personListViewModel(): PersonListViewModel = KoinPlatform.getKoin().get()

fun issPositionViewModel(): ISSPositionViewModel = KoinPlatform.getKoin().get()
