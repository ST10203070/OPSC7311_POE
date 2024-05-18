package com.example.opsc7311_poe

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.opsc7311_poe.databinding.ActivityDataVisualisationBinding
import java.text.SimpleDateFormat
import java.util.*

class DataVisualisationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataVisualisationBinding
    private lateinit var timesheetEntriesAdapter: TimesheetEntriesAdapter
    private val timesheetEntries = mutableListOf<TimeEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataVisualisationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timesheetEntriesAdapter = TimesheetEntriesAdapter(timesheetEntries)
        binding.timeEntriesListView.adapter = timesheetEntriesAdapter

        // Set up date picker button click listeners
        binding.startDateButton.setOnClickListener {
            showDatePickerDialog { date -> binding.startDateValue.text = date }
        }
        binding.endDateButton.setOnClickListener {
            showDatePickerDialog { date -> binding.endDateValue.text = date }
        }

        // Load initial data
        loadTimesheetEntriesAndCategorySummary()

        // Set goals click listener
        binding.buttonDailyGoals.setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        // Time Entry click listener
        binding.buttonNewTimeEntry.setOnClickListener {
            startActivity(Intent(this, TimeEntryActivity::class.java))
        }
    }

    private fun loadTimesheetEntriesAndCategorySummary() {
        val startDate = binding.startDateValue.text.toString()
        val endDate = binding.endDateValue.text.toString()

        loadTimesheetEntries(startDate, endDate)
        loadCategorySummary(startDate, endDate)
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = formatDate(year, monthOfYear, dayOfMonth)
                onDateSelected(selectedDate)
                loadTimesheetEntriesAndCategorySummary()
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun formatDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        val dateFormat = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    inner class TimesheetEntriesAdapter(private val timesheetEntries: List<TimeEntry>) : BaseAdapter() {
        override fun getCount(): Int {
            return timesheetEntries.size
        }

        override fun getItem(position: Int): Any {
            return timesheetEntries[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_time_entry, parent, false)

            val timeEntry = getItem(position) as TimeEntry

            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)

            dateTextView.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(timeEntry.date)
            descriptionTextView.text = timeEntry.description

            return view
        }
    }

    private fun loadTimesheetEntries(startDate: String, endDate: String) {
        timesheetEntries.clear()

        // Add dummy data for testing
        timesheetEntries.add(
            TimeEntry(
                id = 1,
                date = Date(),
                startTime = Date(),
                endTime = Date(),
                description = "Example 1",
                note = "Note 1",
                categoryId = 1,
                photoPath = null,
                totalHours = 2.0f
            )
        )
        timesheetEntries.add(
            TimeEntry(
                id = 2,
                date = Date(),
                startTime = Date(),
                endTime = Date(),
                description = "Example 2",
                note = "Note 2",
                categoryId = 2,
                photoPath = null,
                totalHours = 3.0f
            )
        )
        timesheetEntries.add(
            TimeEntry(
                id = 3,
                date = Date(),
                startTime = Date(),
                endTime = Date(),
                description = "Example 3",
                note = "Note 3",
                categoryId = 3,
                photoPath = null,
                totalHours = 4.0f
            )
        )

        timesheetEntriesAdapter.notifyDataSetChanged()
    }

    private fun loadCategorySummary(startDate: String, endDate: String) {
        // Add dummy data for now
        binding.category1.text = "Category 1      2"
        binding.category2.text = "Category 2      3"
        binding.category3.text = "Category 3      4"

        // Update chart placeholder
        binding.chartImageView.setImageResource(R.drawable.logo/*ic_chart_placeholder*/)
    }
}

//COLBY
//Implement the summary views allowing users to view the list of all timesheet entries created during a user-selectable period. If a photo was stored for an entry, the user must be able to access it from this list
// Implement the view of the total number of hours spent on each category during that period.
