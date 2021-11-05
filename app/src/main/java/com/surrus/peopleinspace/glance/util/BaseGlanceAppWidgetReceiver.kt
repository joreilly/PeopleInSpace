package com.surrus.peopleinspace.glance.util

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.component.KoinComponent

abstract class BaseGlanceAppWidgetReceiver<T : BaseGlanceAppWidget<*>> : GlanceAppWidgetReceiver(),
    KoinComponent {
    override val glanceAppWidget: GlanceAppWidget
        get() {
            return createWidget().apply {
                this.initiateLoad()
            }
        }

    abstract fun createWidget(): T
}