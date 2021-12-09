package com.surrus.common.repository

import com.surrus.common.di.PeopleInSpaceDatabaseWrapper
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        PeopleInSpaceDatabaseWrapper(null)
    }
}
