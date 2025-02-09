package com.cx0014.crwidget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class AppWidgetReceiver : GlanceAppWidgetReceiver() {
    override var glanceAppWidget: GlanceAppWidget = AppWidget()
}