package com.cx0014.crwidget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale.ENGLISH

object UIElements {

    @Composable
    fun TopBarActions() {

        var expanded by remember { mutableStateOf(false) }
        var aboutDialogActive by remember { mutableStateOf(false) }
        var clearDialogActive by remember { mutableStateOf(false) }

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Main menu for other options"
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Clear All Courses") },
                    onClick = { clearDialogActive = true }
                )
                DropdownMenuItem(
                    text = { Text("About") },
                    onClick = { aboutDialogActive = true }
                )
            }
            if (clearDialogActive) ClearDialog { clearDialogActive = false }
            if (aboutDialogActive) AboutDialog { aboutDialogActive = false }
        }
    }

    @Composable
    fun FloatingAddButton() {

        val defaultCourse by remember { mutableStateOf(Course(
            code = "",
            location = "",
            startTime = LocalTime.parse("00:00"),
            endTime = LocalTime.parse("00:00"),
            dayOfWeek = DayOfWeek.MONDAY
        )) }
        var dialogActive by remember { mutableStateOf(false) }

        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            FloatingActionButton(onClick = { dialogActive = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            if (dialogActive) EditDialog(
                defaultCourse = defaultCourse,
                title = "Add a course",
                icon = Icons.Default.Add,
                onDismissRequest = { dialogActive = false },
                onConfirmation = { CourseList.addCourse(it) },
                validityChecker = { CourseList.checkCourseValidity(it, null) }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ClearDialog(onDismissRequest: () -> Unit) {
        BasicAlertDialog(onDismissRequest = { onDismissRequest() }) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning Icon",
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Clear All Courses",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = "Are you sure? This action cannot be undone.",
                        modifier = Modifier.padding(16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onDismissRequest
                        ) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { CourseList.clearCourses(); onDismissRequest() }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }

    @Composable
    private fun AboutDialog(onDismissRequest: () -> Unit) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info Icon",
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        text = """
                        This app allows you to add a widget to your home screen that shows the next class you have to go to.
                        
                        Note that this app does not account for holidays, exams, or other special events, so don't trust the app completely.
                    """.trimIndent(),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Left,
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TimePickerDialog(
        state: TimePickerState,
        onConfirmation: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)) {
                    TimeInput(state = state)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onDismissRequest
                        ) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { onConfirmation(); onDismissRequest() }
                        ) { Text("OK") }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditDialog(
        defaultCourse: Course,
        title: String,
        icon: ImageVector,
        onDismissRequest: () -> Unit,
        onConfirmation: (Course) -> Unit,
        validityChecker: (Course) -> String?
    ) {

        var course by remember { mutableStateOf(defaultCourse) }
        val startTimePickerState = rememberTimePickerState(0, 0, true)
        val endTimePickerState = rememberTimePickerState(0, 0, true)
        var startTimePickerActive by remember { mutableStateOf(false) }
        var endTimePickerActive by remember { mutableStateOf(false) }
        var dayOfWeekMenuExpanded by remember { mutableStateOf(false) }
        val dayOfWeekMenuScrollState = rememberScrollState()
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Dialog(onDismissRequest = { onDismissRequest() }) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = icon, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    TextField(
                        value = course.code,
                        onValueChange = { course = course.copy(code = it) },
                        label = { Text("Course & Class Code") },
                        singleLine = true
                    )
                    TextField(
                        value = course.location,
                        onValueChange = { course = course.copy(location = it) },
                        label = { Text("Class Location") },
                        singleLine = true
                    )
                    ExposedDropdownMenuBox(
                        expanded = dayOfWeekMenuExpanded,
                        onExpandedChange = { dayOfWeekMenuExpanded = it }
                    ) {
                        TextField(
                            modifier = Modifier.menuAnchor(),
                            value = course.dayOfWeek.getDisplayName(TextStyle.FULL, ENGLISH),
                            onValueChange = {},
                            label = { Text("Day of the Week") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = dayOfWeekMenuExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = dayOfWeekMenuExpanded,
                            onDismissRequest = { dayOfWeekMenuExpanded = false },
                            scrollState = dayOfWeekMenuScrollState
                        ) {
                            DayOfWeek.entries.forEach {
                                DropdownMenuItem(
                                    onClick = {
                                        course.dayOfWeek = it
                                        dayOfWeekMenuExpanded = false
                                    },
                                    text = { Text(it.getDisplayName(TextStyle.FULL, ENGLISH)) }
                                )
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth()) {
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = course.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            onValueChange = {},
                            label = { Text("Starts at") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { startTimePickerActive = true }) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Choose starting time"
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        TextField(
                            modifier = Modifier.weight(1f),
                            value = course.endTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            onValueChange = {},
                            label = { Text("Ends at") },
                            readOnly = true,
                            trailingIcon = {
                                IconButton(onClick = { endTimePickerActive = true }) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Choose ending time"
                                    )
                                }
                            },
                            isError = course.startTime.isAfter(course.endTime)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Display an error message if there is one
                    errorMessage?.let {
                        Text(
                            text = it,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onDismissRequest
                        ) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                errorMessage = validityChecker(course)
                                if (errorMessage == null) {
                                    onConfirmation(course)
                                    onDismissRequest()
                                }
                            }
                        ) { Text("OK") }
                    }
                }
            }
        }

        if (startTimePickerActive) TimePickerDialog(
            state = startTimePickerState,
            onConfirmation = { course.startTime = LocalTime.of(
                startTimePickerState.hour,
                startTimePickerState.minute) },
            onDismissRequest = { startTimePickerActive = false }
        )
        if (endTimePickerActive) TimePickerDialog(
            state = endTimePickerState,
            onConfirmation = { course.endTime = LocalTime.of(
                endTimePickerState.hour,
                endTimePickerState.minute) },
            onDismissRequest = { endTimePickerActive = false }
        )
    }
}