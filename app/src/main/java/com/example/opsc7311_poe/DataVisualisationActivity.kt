package com.example.ops7311_poe

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.example.opsc7311_poe.R
import com.example.opsc7311_poe.TimeEntry
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
        binding.timesheetEntriesListview.adapter = timesheetEntriesAdapter

        // Set up date picker button click listener
        binding.selectDateBtn.setOnClickListener {
            showDatePickerDialog()
        }

        // Load initial data
        loadTimesheetEntriesAndCategorySummary()
    }

    private fun loadTimesheetEntriesAndCategorySummary() {
        val startDate = "2022-01-01"
        val endDate = getEndDate(startDate)

        loadTimesheetEntries(startDate, endDate)
        loadCategorySummary(startDate, endDate)
    }

    private fun showDatePickerDialog() {
        // Implementing DatePickerDialog
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Update EditText with selected date
                val selectedDate = formatDate(year, monthOfYear, dayOfMonth)
                binding.dateView.setText(selectedDate)

                // Reload data
                loadTimesheetEntriesAndCategorySummary()
            },
            year,
            month,
            dayOfMonth
        )
        datePickerDialog.show()
    }

    private fun getEndDate(startDate: String): String {
        // Assuming the end date is the same as the start date for simplicity
        return startDate
    }

    private fun formatDate(year: Int, monthOfYear: Int, dayOfMonth: Int): String {
        // Format the selected date as needed
        val calendar = Calendar.getInstance()
        calendar.set(year, monthOfYear, dayOfMonth)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
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
                .inflate(R.layout.activity_data_visualisation, parent, false)

            val timeEntry = getItem(position) as TimeEntry

            val dateTextView = view.findViewById<TextView>(R.id.etDate)
            val descriptionTextView = view.findViewById<TextView>(R.id.etDescription)

            dateTextView.text = timeEntry.date.toString()
            descriptionTextView.text = timeEntry.description

            return view
        }
    }

    private fun loadTimesheetEntries(startDate: String, endDate: String) {
        timesheetEntries.clear()



        timesheetEntriesAdapter = TimesheetEntriesAdapter(timesheetEntries)
        binding.timesheetEntriesListview.adapter = timesheetEntriesAdapter
    }

    private fun loadCategorySummary(startDate: String, endDate: String) {
        // Add dummy data for now
        val categorySummary = "Total hours: 10.0, Total entries: 2"
        binding.categorySummaryTextview.text = categorySummary
    }
}

//COLBY
//Implement the summary views allowing users to view the list of all timesheet entries created during a user-selectable period. If a photo was stored for an entry, the user must be able to access it from this list
// Implement the view of the total number of hours spent on each category during that period.
