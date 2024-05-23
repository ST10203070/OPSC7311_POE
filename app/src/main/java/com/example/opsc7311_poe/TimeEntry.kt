package com.example.opsc7311_poe

import java.util.Date

/**
 * Data class representing a single time entry.
 */
data class TimeEntry(
   /* val id: Int, // Unique identifier for the time entry
    val date: Date, // The date of the work done
    val startTime: Date, // Start time of the work
    val endTime: Date, // End time of the work
    val description: String, // Description of the work done
    val note: String,
    val categoryId: Int, // ID of the category this entry belongs to
    val photoPath: String?, // Optional path to a photo related to the entry
    val totalHours: Float*/ // Total hours worked for this entry

   val id: String = "",
   val category: String = "",
   val date: String = "",
   val description: String = "",
   val endTime: String = "",
   val hours: Double = 0.0,
   val note: String = "",
   val photo: String = "",
   val startTime: String? = "",
   val username: String = ""
)