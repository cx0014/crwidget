package com.cx0014.crwidget

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@HiltWorker
internal class AppWidgetWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Update the widget
            val nextCourse = CourseList.nextCourse()
            updateWidget(nextCourse, appContext)

            // If there's no more courses for today, update again tomorrow
            val waitDuration = if (nextCourse == null) Duration.between(
                LocalTime.now().atDate(LocalDate.now()),
                LocalTime.of(0, 0).atDate(LocalDate.now().plusDays(1))
            ) else Duration.between(LocalTime.now(), nextCourse.startTime)

            // You should ExistingWorkPolicy.REPLACE yourself now!
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                "AppWidgetWorker",
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<AppWidgetWorker>()
                    .setInitialDelay(waitDuration).build()
            )

            // Return a success!
            Result.success()
        } catch (e: Exception) { Result.retry() }
    }

    private suspend fun updateWidget(nextCourse: Course?, context: Context) {
        GlanceAppWidgetManager(context).getGlanceIds(AppWidget::class.java).forEach { glanceId ->  
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[stringPreferencesKey("nextCourse")] = Json.encodeToString(nextCourse)
            }
        }
        AppWidget().updateAll(context)
    }
}