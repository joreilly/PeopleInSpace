package dev.johnoreilly.peopleinspace.di

import android.util.Log
import coil.ImageLoader
import coil.util.DebugLogger
import dev.johnoreilly.peopleinspace.BuildConfig
import dev.johnoreilly.peopleinspace.list.PersonListViewModel
import dev.johnoreilly.peopleinspace.map.MapViewModel
import dev.johnoreilly.peopleinspace.person.PersonDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
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
            .respectCacheHeaders(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Log.VERBOSE))
                }
            }
            .build()
    }
}
