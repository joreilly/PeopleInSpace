package com.surrus.peopleinspace.di

import android.util.Log
import coil.ImageLoader
import coil.util.DebugLogger
import com.surrus.peopleinspace.BuildConfig
import com.surrus.peopleinspace.list.PersonListViewModel
import com.surrus.peopleinspace.map.MapViewModel
import com.surrus.peopleinspace.person.PersonDetailsViewModel
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
