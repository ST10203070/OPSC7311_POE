package com.example.opsc7311_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalsBinding
    private val firestoreRepository = FirestoreRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup listeners or handlers for goal setting
        setupGoalSetting()
    }

    private fun setupGoalSetting() {
        // Implement logic for users to set goals
    }
}

//NYASHA
//Implement goal setting functionality, including UI and logic for setting minimum and maximum daily goals for hours worked.