package dev.johnoreilly.common.di

import app.cash.sqldelight.db.SqlDriver
import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
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
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.mp.KoinPlatform
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
object KoinApp

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration? = null) =
    startKoin<KoinApp> {
        includes(appDeclaration)
    }

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false)

// helpers for iOS/Swift clients to resolve view models from Koin
// (composition-root service location so view models can use constructor injection)
fun personListViewModel(): PersonListViewModel = KoinPlatform.getKoin().get()
fun issPositionViewModel(): ISSPositionViewModel = KoinPlatform.getKoin().get()

@Configuration
@Module(includes = [CommonModule::class])
class AppModule

@Module(includes = [NativeModule::class])
@ComponentScan("dev.johnoreilly.common")
class CommonModule {
    @Single
    fun json() = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    fun httpClient(httpClientEngine: HttpClientEngine, json : Json) = createHttpClient(httpClientEngine, json, true)

    @Single
    fun dispatcher() = CoroutineScope(Dispatchers.Default + SupervisorJob() )
}

class PeopleInSpaceDatabaseWrapper(val driver: SqlDriver, val instance: PeopleInSpaceDatabase)

expect class ContextWrapper

@Module
expect class NativeModule() {

    @Single
    fun providesContextWrapper(scope : Scope) : ContextWrapper

    @Single
    fun getHttpClientEngine(): HttpClientEngine

    @Single
    fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper): PeopleInSpaceDatabaseWrapper
}

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