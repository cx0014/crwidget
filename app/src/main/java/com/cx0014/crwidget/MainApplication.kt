package com.cx0014.crwidget

import android.app.Application
import android.content.Context

class MainApplication: Application() {

    companion object { lateinit var appContext: Context }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        CourseList.initialize(filesDir)
    }
}