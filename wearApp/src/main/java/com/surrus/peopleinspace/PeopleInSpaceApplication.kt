package com.surrus.peopleinspace

import android.app.Application
import android.util.Log
import co.touchlab.kermit.Logger
import coil.ImageLoader
import coil.util.DebugLogger
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.logger.Level
import org.koin.dsl.module

class PeopleInSpaceApplication : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@PeopleInSpaceApplication)

            modules(imageLoader(this@PeopleInSpaceApplication))
            modules(appModule)
        }

        Logger.d { "PeopleInSpaceApplication" }
    }
}

fun imageLoader(application: PeopleInSpaceApplication) = module {
    single {
        ImageLoader.Builder(application)
            .crossfade(true)
            .respectCacheHeaders(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Log.VERBOSE))
                }
            }
            .build()
    }
}
