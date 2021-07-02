package com.surrus.peopleinspace

import android.app.Application
import co.touchlab.kermit.Kermit
import com.surrus.common.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PeopleInSpaceApplication : Application(), KoinComponent {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)
        }

        logger.d { "PeopleInSpaceApplication" }
    }
}
