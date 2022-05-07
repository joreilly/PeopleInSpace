package com.surrus.peopleinspace

import android.app.Application
import co.touchlab.kermit.Logger
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.wearAppModule
import com.surrus.peopleinspace.di.wearImageLoader
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.logger.Level

class PeopleInSpaceApplication : Application(), KoinComponent, ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@PeopleInSpaceApplication)

            modules(wearImageLoader)
            modules(wearAppModule)
        }

        Logger.d { "PeopleInSpaceApplication" }
    }

    override fun newImageLoader(): ImageLoader = get()
}

