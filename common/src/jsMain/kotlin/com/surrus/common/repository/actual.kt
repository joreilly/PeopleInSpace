package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        PeopleInSpaceDatabaseWrapper(null)
    }

    single<Logger> { CommonLogger() }
}
