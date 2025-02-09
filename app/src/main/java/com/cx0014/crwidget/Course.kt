package com.cx0014.crwidget

import kotlinx.serialization.Serializable
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * A class to hold all the necessary information about a course
 * @param code The shortened code for a course (e.g. "COMP2011 (L2)")
 * @param location The location where the class is held
 * @param startTime When the class starts
 * @param endTime When the class ends
 * @param dayOfWeek Which day of the week the class is held on
 */
@Serializable
data class Course(
    var code: String,
    var location: String,
    @Serializable(with = TimeSerializer::class) var startTime: LocalTime,
    @Serializable(with = TimeSerializer::class) var endTime: LocalTime,
    var dayOfWeek: DayOfWeek
)