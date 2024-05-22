package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
    private var anyChartView: AnyChartView? = null
    private var cartesian: Cartesian? = null

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
     //  anyChartView = findViewById(R.id.barChart)
        updateBarChart()
    }

    override fun onResume() {
        super.onResume()
        updateBarChart()
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
                clearChart()
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
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
    }

    private fun clearChart() {
        anyChartView?.clear()
    }

    private fun updateBarChart() {
        // Initialize AnyChartView here
        anyChartView = findViewById(R.id.barChart)

        firestoreRepository.getGoalsData(username) { goalsData, minGoal, maxGoal ->
            // Create a new chart instance
            cartesian = AnyChart.column()

            cartesian?.animation(true)
            cartesian?.title("Goals Analysis Over Past Month")
            cartesian?.yScale()?.minimum(0.0)
            cartesian?.tooltip()
                ?.positionMode(TooltipPositionMode.POINT)
                ?.anchor(Anchor.CENTER_BOTTOM)
                ?.position(Position.CENTER_BOTTOM)
                ?.offsetX(0.0)
                ?.offsetY(5.0)
            cartesian?.interactivity()?.hoverMode(HoverMode.BY_X)
            cartesian?.xAxis(0)?.title("Date")
            cartesian?.yAxis(0)?.title("Hours")

            val data: MutableList<DataEntry> = ArrayList()
            for ((date, hours) in goalsData) {
                data.add(ValueDataEntry(date, hours))
            }

            // Create column series for the bar chart
            val column: Column? = cartesian?.column(data)
            column?.tooltip()
                ?.titleFormat("{%X}")
                ?.position(Position.CENTER_BOTTOM)
                ?.anchor(Anchor.CENTER_BOTTOM)
                ?.offsetX(0.0)
                ?.offsetY(5.0)
                ?.format("{%Value}{groupsSeparator: }")

            // Calculate current date and date a month prior
            val endDate = Calendar.getInstance()
            val startDate = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
            val endDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(endDate.time)
            val startDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(startDate.time)

            // Add markers for min and max goals
            val minGoalMarker = cartesian?.lineMarker(0)
            minGoalMarker?.value(minGoal)
            minGoalMarker?.stroke("3 #00FF00")

            val maxGoalMarker = cartesian?.lineMarker(1)
            maxGoalMarker?.value(maxGoal)
            maxGoalMarker?.stroke("3 #FF0000")

            // Adjust y-axis range to fit the data
            val yAxis = cartesian?.yScale()
            val maxYValue = maxOf(maxGoal, data.maxOfOrNull { (it as ValueDataEntry).getValue("value") as? Double ?: 0.0 } ?: 0.0)
            yAxis?.maximum(maxYValue + 1) // Adding a little padding above the maximum value
            yAxis?.minimum(0.0)

            // Set the new chart instance to the AnyChartView
            anyChartView?.setChart(cartesian)
            anyChartView?.invalidate()
        }
    }
}
