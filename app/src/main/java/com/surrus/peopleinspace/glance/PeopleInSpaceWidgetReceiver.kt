package com.surrus.peopleinspace.glance

import com.surrus.peopleinspace.glance.util.BaseGlanceAppWidgetReceiver

class PeopleInSpaceWidgetReceiver : BaseGlanceAppWidgetReceiver<PeopleInSpaceWidget>() {
    override fun createWidget(): PeopleInSpaceWidget = PeopleInSpaceWidget()
}