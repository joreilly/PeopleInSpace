package com.surrus.peopleinspace

import android.app.Application
import com.surrus.common.di.commonModule
import com.surrus.common.di.initKoin
import com.surrus.common.repository.appContext
import com.surrus.peopleinspace.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class PeopleInSpaceApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = this

        initKoin {
            androidLogger()
            androidContext(this@PeopleInSpaceApplication)
            modules(appModule, commonModule)
        }
    }
}