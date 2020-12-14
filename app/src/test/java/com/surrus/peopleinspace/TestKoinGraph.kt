package com.surrus.peopleinspace

import android.content.Context
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import org.junit.Test
import org.koin.test.check.checkModules
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.surrus.common.repository.appContext
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestKoinGraph  {
    private val context = getApplicationContext<Context>()

    @Test
    fun `checking koin modules`() {
        appContext = context

        initKoin {
            androidContext(context)
            modules(appModule)
        }.checkModules {  }
    }
}