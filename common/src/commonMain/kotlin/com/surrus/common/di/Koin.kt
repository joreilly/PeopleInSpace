package com.surrus.common.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.normalizedCache
import com.surrus.common.repository.PeopleInSpaceRepository
import com.surrus.common.repository.PeopleInSpaceRepositoryInterface
import com.surrus.common.repository.platformModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(enableNetworkLogs = enableNetworkLogs), platformModule())
    }

// called by iOS etc
fun initKoin() = initKoin(enableNetworkLogs = false) {}

fun commonModule(enableNetworkLogs: Boolean) = module {
    single { CoroutineScope(Dispatchers.Default + SupervisorJob() ) }

    single<PeopleInSpaceRepositoryInterface> { PeopleInSpaceRepository() }


    single { createApolloClient(get()) }
}



fun createApolloClient(sqlNormalizedCacheFactory: NormalizedCacheFactory): ApolloClient {
    val memoryFirstThenSqlCacheFactory = MemoryCacheFactory(10 * 1024 * 1024)
        .chain(sqlNormalizedCacheFactory)

    return ApolloClient.Builder()
        .serverUrl("https://peopleinspace-graphql-guhrsfr7ka-uc.a.run.app/graphql")
        .webSocketServerUrl("wss://peopleinspace-graphql-guhrsfr7ka-uc.a.run.app/subscriptions")
        .normalizedCache(memoryFirstThenSqlCacheFactory, writeToCacheAsynchronously = true)
        .build()
}
