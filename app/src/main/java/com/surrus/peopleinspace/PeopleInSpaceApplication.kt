package com.surrus.peopleinspace

import android.app.Application
import co.touchlab.kermit.Kermit
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import org.osmdroid.config.Configuration
import java.io.File

class PeopleInSpaceApplication : Application() {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        // needed for osmandroid
        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "osm").also {
            it.mkdir()
        }

        initKoin {
            // https://github.com/InsertKoinIO/koin/issues/1188
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@PeopleInSpaceApplication)
            modules(appModule)
        }

        logger.d { "PeopleInSpaceApplication" }
    }
}
