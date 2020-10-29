package com.surrus.peopleinspace

import android.app.Application
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger
import com.surrus.common.di.commonModule
import com.surrus.common.di.initKoin
import com.surrus.common.repository.appContext
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.inject

class PeopleInSpaceApplication : Application(), KoinComponent {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        appContext = this

        initKoin {
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)
            modules(appModule, commonModule)
        }

        logger.d { "PeopleInSpaceApplication" }
    }
}