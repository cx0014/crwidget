package com.cx0014.crwidget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.cx0014.crwidget.UIElements.EditDialog
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

object CourseList {

    private lateinit var courseFile: File
    private var courseList = mutableStateListOf<Course>()

    /**
     * Opens the courses.json file and parses its contents.
     * Both parameters are provided by [MainApplication]
     */
    fun initialize(filesDir: File) {
        try {
            courseFile = File(filesDir, "courses.json")
            if (!courseFile.exists()) courseFile.createNewFile()
            courseList = (Json.decodeFromString<List<Course>>(courseFile.readText())).toMutableStateList()
            enqueueWork()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun saveToFile() = courseFile.writeText(Json.encodeToString(courseList.toList()))

    private fun enqueueWork() = WorkManager
        .getInstance(MainApplication.appContext)
        .enqueueUniqueWork(
            "AppWidgetWorker",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(AppWidgetWorker::class.java)
        )

    fun addCourse(inputCourse: Course) {
        courseList += inputCourse
        courseList.sortWith(compareBy(Course::dayOfWeek, Course::startTime))
        saveToFile()
        enqueueWork()
    }

    private fun removeCourse(inputCourse: Course) {
        courseList.remove(inputCourse)
        saveToFile()
        enqueueWork()
    }

    private fun replaceCourse(oldCourse: Course, newCourse: Course) {
        courseList.remove(oldCourse)
        addCourse(newCourse)
    }

    fun clearCourses() {
        courseList.clear()
        saveToFile()
        enqueueWork()
    }

    fun nextCourse(): Course? {
        // Find the courses which are on the current day of the week
        val currentDayCourses = courseList.filter { course ->
            course.dayOfWeek == LocalDate.now().dayOfWeek }.toMutableList()

        // Find the most recent course that has a start time after the current time
        currentDayCourses.sortBy { it.startTime }
        currentDayCourses.forEach { course ->
            if (course.startTime.isAfter(LocalTime.now())) return course
        }

        // Return nothing if there are no courses after the current one
        return null
    }

    /**
     * Check if the add/edit course operation is valid
     * @param newCourse The course to be added/edited into the list
     * @param oldCourse The course that [newCourse] is replacing using the edit function
     * @return Nothing if successful, an error message otherwise
     */
    fun checkCourseValidity(newCourse: Course, oldCourse: Course?): String? {

        if(newCourse.startTime.isAfter(newCourse.endTime))
            return "The starting time is after the ending time"

        val filteredCourseList = courseList.filter { course ->
            course.dayOfWeek == newCourse.dayOfWeek }

        for (course in filteredCourseList) {

            // Skip over the course that newCourse is replacing
            if (course == oldCourse) continue

            if (newCourse.startTime.isAfter(course.startTime) and
                newCourse.startTime.isBefore(course.endTime))
                return "Starting time is in between a course's duration"

            if (newCourse.endTime.isAfter(course.startTime) and
                newCourse.endTime.isBefore(course.endTime))
                return "Ending time is in between a course's duration"

            if (newCourse.startTime.isBefore(course.startTime) and
                newCourse.endTime.isAfter(course.endTime))
                return "Complete overlap with another course"
        }

        // No problems detected, so nothing is returned
        return null
    }

    @Composable
    fun CourseList(padding: PaddingValues) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .wrapContentHeight()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            courseList
                .groupBy { it.dayOfWeek }
                .forEach { (dayOfWeek, weekdayCourseList) ->
                    Text(
                        text = dayOfWeek.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    weekdayCourseList.forEach { course ->
                        var dialogActive by remember { mutableStateOf(false) }
                        ListItem(
                            headlineContent = { Text(course.code, style = MaterialTheme.typography.titleMedium) },
                            supportingContent = { Text("${course.location}, ${course.startTime} ~ ${course.endTime}") },
                            trailingContent = {
                                IconButton(onClick = { removeCourse(course) }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Remove course")
                                }
                            },
                            modifier = Modifier.clickable { dialogActive = true }
                        )
                        if (dialogActive) EditDialog(
                            defaultCourse = course,
                            title = "Edit ${course.code}",
                            icon = Icons.Default.Edit,
                            onDismissRequest = { dialogActive = false },
                            onConfirmation = { replaceCourse(course, it) },
                            validityChecker = { checkCourseValidity(it, course) }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
        }
    }
}