package com.surrus.peopleinspace

import android.app.Application
import co.touchlab.kermit.Logger
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level
import org.osmdroid.config.Configuration
import java.io.File

class PeopleInSpaceApplication : Application() {

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

        Logger.d { "PeopleInSpaceApplication" }
    }
}
