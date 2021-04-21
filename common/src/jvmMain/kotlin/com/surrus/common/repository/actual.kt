package com.surrus.common.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import org.koin.dsl.module

actual fun getLogger(): Logger = CommonLogger()

actual fun platformModule() = module {
    single {
    }
}