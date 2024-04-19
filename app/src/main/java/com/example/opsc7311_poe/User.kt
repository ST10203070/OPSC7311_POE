package com.example.opsc7311_poe

/**
 * Data class representing a user with a username, password, minDailyGoal, and maxDailyGoal.
 */
data class User(
    val username: String = "",
    val password: String = "",
    val minDailyGoal: Float? = null,
    val maxDailyGoal: Float? = null
)

