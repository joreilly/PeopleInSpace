package dev.johnoreilly.common.di

import app.cash.sqldelight.db.SqlDriver
import dev.johnoreilly.common.remote.AstroviewerApi
import dev.johnoreilly.common.remote.PeopleInSpaceApi
import dev.johnoreilly.common.repository.PeopleInSpaceRepository
import dev.johnoreilly.common.repository.PeopleInSpaceRepositoryInterface
import dev.johnoreilly.peopleinspace.db.PeopleInSpaceDatabase
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration? = null) =
    startKoin {
        includes(appDeclaration)
        modules(commonModule(enableNetworkLogs), nativeModule())
    }

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false)

private fun commonModule(enableNetworkLogs: Boolean) = module {
    single { Json { isLenient = true; ignoreUnknownKeys = true } }
    single { createHttpClient(get(), get(), enableNetworkLogs) }
    single { CoroutineScope(Dispatchers.Default + SupervisorJob()) }
    single { PeopleInSpaceApi(get()) }
    single { AstroviewerApi(get()) }
    single<PeopleInSpaceRepositoryInterface> {
        PeopleInSpaceRepository(
            peopleInSpaceApi = get(),
            peopleInSpaceDatabase = get(),
            astroviewerApi = get(),
            coroutineScope = get(),
        )
    }
}

class PeopleInSpaceDatabaseWrapper(val driver: SqlDriver, val instance: PeopleInSpaceDatabase)

expect fun nativeModule(): Module

fun createHttpClient(httpClientEngine: HttpClientEngine, json: Json, enableNetworkLogs: Boolean) = HttpClient(httpClientEngine) {
    install(ContentNegotiation) {
        json(json)
    }
    if (enableNetworkLogs) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
