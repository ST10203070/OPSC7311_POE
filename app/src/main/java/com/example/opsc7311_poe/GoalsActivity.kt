package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.charts.Cartesian
import com.anychart.core.cartesian.series.Column
import com.anychart.enums.Anchor
import com.anychart.enums.HoverMode
import com.anychart.enums.Position
import com.anychart.enums.TooltipPositionMode
import com.example.opsc7311_poe.databinding.ActivityGoalsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GoalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalsBinding
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var username: String
    private lateinit var anyChartView: AnyChartView
    private lateinit var cartesian: Cartesian

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
                Toast.makeText(this, "Please enter valid goals (min < max)", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (username.isEmpty()) {
                Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firestoreRepository.saveUserGoals(username, minGoal, maxGoal, {
                Toast.makeText(this, "Goals saved successfully", Toast.LENGTH_SHORT).show()
                clearGoalFields()
                updateBarChart()
            }, { exception ->
                Toast.makeText(
                    this,
                    "Failed to save goals: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
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
        cartesian = AnyChart.column()
        cartesian.animation(true)

        cartesian.title("Goals Analysis Over Past Month")

        cartesian.yScale().minimum(0.0)

        cartesian.tooltip()
            .positionMode(TooltipPositionMode.POINT)
            .anchor(Anchor.CENTER_BOTTOM)
            .position(Position.CENTER_BOTTOM)
            .offsetX(0.0)
            .offsetY(5.0)

        cartesian.interactivity().hoverMode(HoverMode.BY_X)

        cartesian.xAxis(0).title("Date")
        cartesian.yAxis(0).title("Hours")

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
            val column: Column = cartesian.column(data)

            column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0.0)
                .offsetY(5.0)
                .format("{%Value}{groupsSeparator: }")

            // Calculate current date and date a month prior
            val endDate = Calendar.getInstance()
            val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }

            val endDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate.time)
            val startDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate.time)

            // Fetch min and max goals
            firestoreRepository.getUserGoals(username) { minGoal, maxGoal ->
                // Add min and max goal lines
                val minGoalLine = cartesian.line(listOf(
                    ValueDataEntry(startDateString, minGoal),
                    ValueDataEntry(endDateString, minGoal)
                ))
                minGoalLine.stroke("5 #00FF00")

                val maxGoalLine = cartesian.line(listOf(
                    ValueDataEntry(startDateString, maxGoal),
                    ValueDataEntry(endDateString, maxGoal)
                ))
                maxGoalLine.stroke("5 #FF0000")

                // Set up the chart with the data and lines
                anyChartView.setChart(cartesian)
                anyChartView.invalidate()
            }
        }
    }
}
