package com.surrus.common.repository

import co.touchlab.kermit.LogcatLogger
import co.touchlab.kermit.Logger
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
    }
}

actual fun getLogger(): Logger = LogcatLogger()