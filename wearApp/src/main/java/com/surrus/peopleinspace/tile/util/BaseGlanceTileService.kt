package com.surrus.peopleinspace.tile.util

import androidx.compose.runtime.Composable
import androidx.glance.wear.GlanceTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

abstract class BaseGlanceTileService<T> : GlanceTileService() {
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