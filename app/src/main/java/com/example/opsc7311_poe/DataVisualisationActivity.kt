package com.example.opsc7311_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityDataVisualisationBinding

class DataVisualisationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataVisualisationBinding
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataVisualisationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load timesheet entries
        loadTimesheetEntries()

        // Load category summary
        loadCategorySummary()
    }

    private fun loadTimesheetEntries() {
        // Logic to retrieve and display timesheet entries
    }

    // Method to load categories and their associated time
    private fun loadCategorySummary() {
        // Logic to retrieve and display summary
    }
}


//COLBY
//Implement the summary views allowing users to view the list of all timesheet entries created during a user-selectable period. If a photo was stored for an entry, the user must be able to access it from this list
// Implement the view of the total number of hours spent on each category during that period.