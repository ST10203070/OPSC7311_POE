package com.example.opsc7311_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityDashboardBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnNewEntry.setOnClickListener {
            startActivity(Intent(this, TimeEntryActivity::class.java))
        }

        binding.btnSetGoals.setOnClickListener {
            startActivity(Intent(this, GoalSettingActivity::class.java))
        }

        binding.btnViewEntries.setOnClickListener {
            startActivity(Intent(this, TimesheetListActivity::class.java))
        }

        binding.btnViewCategorySummary.setOnClickListener {
            startActivity(Intent(this, CategorySummaryActivity::class.java))
        }
    }
}