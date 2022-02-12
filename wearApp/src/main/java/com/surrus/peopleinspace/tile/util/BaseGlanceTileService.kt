package com.surrus.peopleinspace.tile.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.wear.tiles.GlanceTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseGlanceTileService<T> : GlanceTileService(), KoinComponent {
    val context: Context by inject()

    @Composable
    override fun Content() {
        // Terrible hack for lack of suspend load function
        val data = runBlocking(Dispatchers.IO) {
            try {
                loadData()
            } finally {
            }
        }

        Content(data = data)
    }

    abstract suspend fun loadData(): T

    @Composable
    abstract fun Content(data: T)
}