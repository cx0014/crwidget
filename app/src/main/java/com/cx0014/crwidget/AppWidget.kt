package com.cx0014.crwidget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.padding
import androidx.glance.layout.wrapContentSize
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.serialization.json.Json

class AppWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) =
        provideContent { AppWidgetContent() }

    @Composable
    private fun AppWidgetContent() {

        val state = currentState<Preferences>()
        val nextCourseString = state[stringPreferencesKey("nextCourse")]
        val nextCourse: Course? = if (nextCourseString != "")
            nextCourseString?.let { Json.decodeFromString(it) } else null

        val normalTextStyle = TextStyle(color = GlanceTheme.colors.onSurface)

        GlanceTheme {
            Column(
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.widgetBackground)
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                nextCourse?.let{
                    Column(GlanceModifier.wrapContentSize()) {
                        Text(
                            text = nextCourse.code,
                            style = normalTextStyle.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = """
                                ${nextCourse.location}
                                ${nextCourse.startTime} ~ ${nextCourse.endTime}
                            """.trimIndent(),
                            style = normalTextStyle
                        )
                    }
                } ?: run {
                    Text(
                        text = "There are no more courses for today",
                        style = normalTextStyle.copy(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}