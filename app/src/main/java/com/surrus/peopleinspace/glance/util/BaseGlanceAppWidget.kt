package com.surrus.peopleinspace.glance.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.DpSize
import androidx.glance.GlanceId
import androidx.glance.LocalGlanceId
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseGlanceAppWidget<T>(initialData: T? = null) : GlanceAppWidget(), KoinComponent {
    val context: Context by inject()

    var glanceId by mutableStateOf<GlanceId?>(null)
    var size by mutableStateOf<DpSize?>(null)
    var data by mutableStateOf<T?>(initialData)

    private val coroutineScope = MainScope()

    abstract suspend fun loadData(): T

    fun initiateLoad() {
        coroutineScope.launch {
            data = loadData()

            val currentGlanceId = snapshotFlow { glanceId }.filterNotNull().firstOrNull()

            if (currentGlanceId != null) {
                update(context, currentGlanceId)
            }
        }
    }

    @Composable
    override fun Content() {
        glanceId = LocalGlanceId.current
        size = LocalSize.current

        Content(data)
    }

    @Composable
    abstract fun Content(data: T?)
}