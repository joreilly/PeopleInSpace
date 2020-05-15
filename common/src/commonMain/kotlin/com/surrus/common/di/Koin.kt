package com.surrus.common.di

import com.surrus.common.remote.PeopleInSpaceApi
import com.surrus.common.repository.PeopleInSpaceRepository
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(commonModule)
}

// called by iOS etc
fun initKoin() = initKoin{}

val commonModule = module {
    single { PeopleInSpaceRepository() }
    single { PeopleInSpaceApi() }
}
