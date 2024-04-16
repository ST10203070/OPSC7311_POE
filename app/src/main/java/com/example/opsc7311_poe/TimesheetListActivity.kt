package com.example.opsc7311_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityTimesheetListBinding

class TimesheetListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimesheetListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimesheetListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load timesheet entries
        loadTimesheetEntries()
    }

    private fun loadTimesheetEntries() {
        // Logic to retrieve and display timesheet entries
    }
}