package dev.johnoreilly.peopleinspace.peopleinspace.di

import coil3.ImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import coil3.util.Logger
import dev.johnoreilly.peopleinspace.peopleinspace.list.PersonListViewModel
import dev.johnoreilly.peopleinspace.peopleinspace.map.MapViewModel
import dev.johnoreilly.peopleinspace.peopleinspace.person.PersonDetailsViewModel
import dev.johnoreilly.peopleinspace.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
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
            .components {
                add(KtorNetworkFetcherFactory(HttpClient {
                    defaultRequest {
                        // some image hosts (e.g. Wikimedia) reject requests with a
                        // generic library User-Agent
                        header(HttpHeaders.UserAgent, "PeopleInSpace")
                    }
                }))
            }
            .crossfade(true)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Logger.Level.Info))
                }
            }
            .build()
    }
}
