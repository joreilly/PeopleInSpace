package dev.johnoreilly.peopleinspace

import android.app.Application
import co.touchlab.kermit.Logger
import dev.johnoreilly.common.di.initKoin
import dev.johnoreilly.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)
            modules(appModule)
        }

        Logger.d { "PeopleInSpaceApplication" }
    }
}
