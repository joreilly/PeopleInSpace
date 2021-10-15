package com.surrus.peopleinspace

import android.app.Application
import android.util.Log
import co.touchlab.kermit.Kermit
import coil.ImageLoader
import coil.util.CoilUtils
import coil.util.DebugLogger
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.LoggingEventListener
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

class PeopleInSpaceApplication : Application(), KoinComponent {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)

            modules(imageLoader(this@PeopleInSpaceApplication))
            modules(appModule)
        }

        logger.d { "PeopleInSpaceApplication" }
    }
}

fun imageLoader(application: PeopleInSpaceApplication) = module {
    single {
        ImageLoader.Builder(application)
            .crossfade(true)
            .okHttpClient {
                val cache = CoilUtils.createDefaultCache(application)

                // Rewrite the Cache-Control header to cache all responses for a week.
                // Not all images have consistent cache headers.
                val cacheControlInterceptor = Interceptor {
                    val response = it.proceed(it.request())

                    if (response.code != 200) {
                        response
                    } else {
                        response.newBuilder()
                            .header("Cache-Control", "max-age=604800,public")
                            .build()
                    }
                }

                // Lazily create the OkHttpClient that is used for network operations.
                OkHttpClient.Builder()
                    .cache(cache)
                    .apply {
                        if (BuildConfig.DEBUG) {
                            eventListenerFactory(LoggingEventListener.Factory {
                                Log.v("OkHttp", it)
                            })
                        }
                    }
                    .addNetworkInterceptor(cacheControlInterceptor)
                    .build()
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger(Log.VERBOSE))
                }
            }
            .build()
    }
}
