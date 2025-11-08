package com.surrus.peopleinspace.remotecompose.util

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

abstract class AsyncAppWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, wm: AppWidgetManager, widgetIds: IntArray) {
        goAsync {
            update(context, wm, widgetIds)
        }
    }

    abstract suspend fun update(context: Context, wm: AppWidgetManager, widgetIds: IntArray)
}