package dev.johnoreilly.peopleinspace

import android.app.Application
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.ktor3.KtorNetworkFetcherFactory
import dev.johnoreilly.common.di.initKoin
import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.osmdroid.config.Configuration
import java.io.File

class PeopleInSpaceApplication : Application(), SingletonImageLoader.Factory {

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
        }

        Logger.d { "PeopleInSpaceApplication" }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory(HttpClient {
                    defaultRequest {
                        // some image hosts (e.g. Wikimedia) reject requests with a
                        // generic library User-Agent
                        header(HttpHeaders.UserAgent, "PeopleInSpace")
                    }
                }))
            }
            .build()
    }
}
