package com.surrus.common.repository

import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory
import com.apollographql.apollo3.cache.normalized.api.NormalizedCacheFactory
import org.koin.dsl.module

actual fun platformModule() = module {
    single<NormalizedCacheFactory> { MemoryCacheFactory() }
}
