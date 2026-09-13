package dev.johnoreilly.common.di

import dev.johnoreilly.common.viewmodel.ISSPositionViewModel
import dev.johnoreilly.common.viewmodel.PersonListViewModel
import io.ktor.client.*
import io.ktor.client.engine.*
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
internal object KoinApp

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

// NativeModule is already pulled in transitively by CommonModule, and Koin dedupes includes at
// runtime. It is listed here as well because the Koin compiler plugin's full-graph validation
// (1.1.0+) only collects modules reachable one level from the @Configuration entry point, so the
// platform definitions would otherwise be reported as KOIN-D001 missing dependencies.
@Configuration
@Module(includes = [CommonModule::class, NativeModule::class])
internal class AppModule

@Module(includes = [NativeModule::class])
@ComponentScan("dev.johnoreilly.common")
internal class CommonModule {
    @Single
    fun json() = Json { isLenient = true; ignoreUnknownKeys = true }

    @Single
    fun httpClient(httpClientEngine: HttpClientEngine, json : Json) = createHttpClient(httpClientEngine, json, true)

    @Single
    fun dispatcher() = CoroutineScope(Dispatchers.Default + SupervisorJob() )
}

internal expect class ContextWrapper

@Module
internal expect class NativeModule() {

    @Single
    fun providesContextWrapper(scope : Scope) : ContextWrapper

    @Single
    fun getHttpClientEngine(): HttpClientEngine

    @Single
    fun getPeopleInSpaceDatabaseWrapper(ctx : ContextWrapper): PeopleInSpaceDatabaseWrapper
}

