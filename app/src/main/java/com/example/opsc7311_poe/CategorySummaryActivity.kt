package com.example.opsc7311_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityCategorySummaryBinding

class CategorySummaryActivity : AppCompatActivity() {
    // Binding object instance with access to the views in the activity_category_summary.xml layout
    private lateinit var binding: ActivityCategorySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initializing the binding object
        binding = ActivityCategorySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialization and setup code goes here
    }

    // Method to load categories and their associated time
    private fun loadCategorySummary() {
        // Logic to retrieve and display summary
    }
}
