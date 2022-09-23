package com.surrus.peopleinspace

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.surrus.common.di.initKoin
import com.surrus.peopleinspace.di.appModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TestKoinGraph {
    private val context = getApplicationContext<Context>()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        Mockito.mock(clazz.java)
    }

    @Test
    fun `checking koin modules`() {
        initKoin {
            androidContext(context)
            modules(appModule)
        }.checkModules() {
            withInstance<SavedStateHandle>()
        }
    }
}
