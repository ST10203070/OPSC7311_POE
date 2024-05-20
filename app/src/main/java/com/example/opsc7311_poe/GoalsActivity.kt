package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.example.opsc7311_poe.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalsBinding
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var username: String
    private lateinit var anyChartView: AnyChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirestoreRepository
        firestoreRepository = FirestoreRepository(this)

        // Retrieve the username from the Intent
        username = intent.getStringExtra("USERNAME") ?: ""

        setupGoalSetting()
        setupNavigation()

        // Initialize AnyChartView
        anyChartView = findViewById(R.id.barChart)
        setupBarChart()
    }

    private fun setupGoalSetting() {
        binding.btnSetGoal.setOnClickListener {
            val minGoal = binding.etMinGoal.text.toString().toDoubleOrNull()
            val maxGoal = binding.etMaxGoal.text.toString().toDoubleOrNull()

            if (minGoal == null || maxGoal == null || minGoal >= maxGoal) {
                Toast.makeText(this, "Please enter valid goals (min < max)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestoreRepository.saveUserGoals(username, minGoal, maxGoal, {
                Toast.makeText(this, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                clearGoalFields()
            }, { exception ->
                Toast.makeText(this, "Failed to save goals: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun clearGoalFields() {
        binding.etMinGoal.text.clear()
        binding.etMaxGoal.text.clear()
    }

    private fun setupNavigation() {
        binding.btnNewTimeEntry.setOnClickListener {
            val intent = Intent(this, TimeEntryActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        binding.btnDataVisualization.setOnClickListener {
            val intent = Intent(this, DataVisualisationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupBarChart() {
        val cartesian: Cartesian = AnyChart.column()
        anyChartView.setChart(cartesian)

        // Load initial data
        updateBarChart()
    }

    private fun updateBarChart() {
        firestoreRepository.getGoalsData(username) { goalsData ->
            val data: MutableList<DataEntry> = ArrayList()
            for ((date, hours) in goalsData) {
                data.add(ValueDataEntry(date, hours))
            }

            val cartesian: Cartesian = AnyChart.column()
            cartesian.data(data)
            anyChartView.setChart(cartesian)
        }
    }
}
