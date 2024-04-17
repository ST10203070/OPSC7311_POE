package com.example.opsc7311_poe

/**
 * Data class representing a user with a username and password.
 */
data class User(
    val username: String,
    val password: String,
    val minDailyGoal: Float, // Minimum daily goal for hours worked
    val maxDailyGoal: Float // Maximum daily goal for hours worked
)