package com.surrus.common.di

import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.repository.PeopleInSpaceRepository
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.common.viewmodel.ISSPositionViewModel
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
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.ksp.generated.module


fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        //modules(commonModule(enableNetworkLogs = enableNetworkLogs), platformModule())
        startKoin {
            modules(
                AppModule().module
            )
            appDeclaration.invoke(this)
        }
    }

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false) {}


@Module(includes = [CommonModule::class, NativeModule::class])
class AppModule


@Module
@ComponentScan("com.surrus.common")
class CommonModule {
    @Single
    fun json() = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    fun httpClient(httpClientEngine: HttpClientEngine, json : Json) = createHttpClient(httpClientEngine, json, true)
}


@Module
expect class NativeModule() {
    @Single
    fun httpClientEngine(): HttpClientEngine
}


//fun commonModule(enableNetworkLogs: Boolean) = module {
//    singleOf(::createJson)
//    single { createHttpClient(get(), get(), enableNetworkLogs = enableNetworkLogs) }
//    singleOf(::PeopleInSpaceApi)
//    singleOf(::PeopleInSpaceRepository).bind<PeopleInSpaceRepositoryInterface>()
//
//    single { CoroutineScope(Dispatchers.Default + SupervisorJob() ) }
//}
//
//fun createJson() = Json { isLenient = true; ignoreUnknownKeys = true }


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
