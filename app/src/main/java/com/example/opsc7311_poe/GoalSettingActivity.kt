package com.example.opsc7311_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityGoalSettingBinding

class GoalSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup listeners or handlers for goal setting
        setupGoalSetting()
    }

    private fun setupGoalSetting() {
        // Implement logic for users to set goals
    }
}
