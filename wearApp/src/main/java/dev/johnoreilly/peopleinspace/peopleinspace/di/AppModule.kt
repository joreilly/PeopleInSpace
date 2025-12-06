package dev.johnoreilly.peopleinspace.peopleinspace.di

import coil3.ImageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import coil3.util.Logger
import dev.johnoreilly.peopleinspace.peopleinspace.list.PersonListViewModel
import dev.johnoreilly.peopleinspace.peopleinspace.map.MapViewModel
import dev.johnoreilly.peopleinspace.peopleinspace.person.PersonDetailsViewModel
import dev.johnoreilly.peopleinspace.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val wearAppModule = module {
    viewModel { (personName: String) ->
        PersonDetailsViewModel(
            personName,
            peopleInSpaceRepository = get(),
        )
    }
    viewModel { PersonListViewModel(peopleInSpaceRepository = get()) }
    viewModel { MapViewModel(peopleInSpaceRepository = get()) }
}

val wearImageLoader = module {
    single {
        ImageLoader.Builder(androidContext())
            .crossfade(true)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Logger.Level.Info))
                }
            }
            .build()
    }
}
