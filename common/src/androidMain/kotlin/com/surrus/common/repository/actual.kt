package com.surrus.common.repository

import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import com.apollographql.apollo3.cache.normalized.sql.SqlNormalizedCacheFactory
import org.koin.dsl.module



actual fun platformModule() = module {
    single<NormalizedCacheFactory> { SqlNormalizedCacheFactory(get(), "swapi.db") }
}
