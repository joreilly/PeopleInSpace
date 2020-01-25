package com.surrus.peopleinspace

import android.app.Application
import com.surrus.common.repository.appContext
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PeopleInSpaceApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this

        startKoin {
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)
            modules(appModule)
        }
    }
}