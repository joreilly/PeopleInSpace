package com.surrus.peopleinspace.glance

import com.surrus.peopleinspace.glance.util.BaseGlanceAppWidgetReceiver

class ISSMapWidgetReceiver : BaseGlanceAppWidgetReceiver<ISSMapWidget>() {
    override fun createWidget(): ISSMapWidget = ISSMapWidget()
}