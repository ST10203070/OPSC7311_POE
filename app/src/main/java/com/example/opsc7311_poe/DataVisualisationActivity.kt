package com.example.opsc7311_poe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.anychart.scales.DateTime
import com.example.opsc7311_poe.databinding.ActivityDataVisualisationBinding
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataVisualisationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataVisualisationBinding
    private lateinit var timesheetEntriesAdapter: TimesheetEntriesAdapter
    private lateinit var categoryHoursAdapter: CategoryHoursAdapter
    private val timesheetEntries = mutableListOf<TimeEntry>()
    private val categoryHours = mutableListOf<Pair<String, Double>>()
    private lateinit var username: String
    private lateinit var firestoreRepository: FirestoreRepository
    private lateinit var photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    companion object {
        private const val READ_MEDIA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataVisualisationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestMediaPermissions()  // Request permissions at the start

        // Initialize FirestoreRepository
        firestoreRepository = FirestoreRepository(this)

        // Retrieve the username from the Intent
        username = intent.getStringExtra("USERNAME") ?: ""

        timesheetEntriesAdapter = TimesheetEntriesAdapter(timesheetEntries)
        categoryHoursAdapter = CategoryHoursAdapter(categoryHours)
        binding.timeEntriesListView.adapter = timesheetEntriesAdapter
        binding.categoryHoursListView.adapter = categoryHoursAdapter

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
            val intent = Intent(this, GoalsActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // Time Entry click listener
        binding.buttonNewTimeEntry.setOnClickListener {
            val intent = Intent(this, TimeEntryActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }

        // Initialize the photo picker launcher
        photoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                handlePhotoUri(it)
            } ?: run {
                Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun handlePhotoUri(uri: Uri) {
        // Handle the URI (e.g., display the photo)
        Log.d("DataVisualisation", "Photo URI: $uri")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(intent)
    }

    private fun requestMediaPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                READ_MEDIA_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_MEDIA_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    private fun loadTimesheetEntriesAndCategorySummary() {
        val startDate = binding.startDateValue.text.toString()
        val endDate = binding.endDateValue.text.toString()

        if (isValidDate(startDate) && isValidDate(endDate)) {
            loadTimesheetEntries(startDate, endDate)
            loadCategorySummary(startDate, endDate)
            loadDailyHoursChart(startDate, endDate) // Load chart data

        } else {
            Toast.makeText(this, "Please select valid start and end dates", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
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
                .inflate(R.layout.list_item_time_entry, parent, false)

            val timeEntry = getItem(position) as TimeEntry

            val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
            val descriptionTextView = view.findViewById<TextView>(R.id.descriptionTextView)
            val noteButton = view.findViewById<Button>(R.id.noteButton)
            val photoButton = view.findViewById<Button>(R.id.photoButton)

            dateTextView.text = timeEntry.date
            descriptionTextView.text = timeEntry.description

            noteButton.setOnClickListener {
                // Show note
                Toast.makeText(parent.context, timeEntry.note, Toast.LENGTH_LONG).show()
            }
            photoButton.setOnClickListener {
                val photoUri = timeEntry.photo
                if (!photoUri.isNullOrEmpty()) {
                    try {
                        Log.d("DataVisualisation", "Opening photo URI: $photoUri")
                        showImageDialog(Uri.parse(photoUri))
                    } catch (e: Exception) {
                        Toast.makeText(parent.context, "Unable to open photo", Toast.LENGTH_SHORT).show()
                        Log.e("DataVisualisation", "Error opening photo: ${e.message}")
                    }
                } else {
                    Toast.makeText(parent.context, "No photo available", Toast.LENGTH_SHORT).show()
                }
            }
            return view
        }
    }

    private fun showImageDialog(uri: Uri) {
        try {
            val dialog = AlertDialog.Builder(this)
            val imageView = ImageView(this)

            // Load the image as a Bitmap
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)

            dialog.setView(imageView)
            dialog.setPositiveButton("Close") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            dialog.show()
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show()
            Log.e("DataVisualisation", "Error loading image: ${e.message}")
        }
    }

    private fun loadTimesheetEntries(startDate: String, endDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateParsed = dateFormat.parse(startDate)
        val endDateParsed = dateFormat.parse(endDate)

        if (startDateParsed != null && endDateParsed != null) {
            firestoreRepository.getTimeEntriesByDateRange(username, startDateParsed, endDateParsed) { entries ->
                timesheetEntries.clear()
                timesheetEntries.addAll(entries)
                timesheetEntriesAdapter.notifyDataSetChanged()
                Log.d("DataVisualisation", "Loaded ${entries.size} entries")
            }
        }
    }

    private fun loadCategorySummary(startDate: String, endDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateParsed = dateFormat.parse(startDate)
        val endDateParsed = dateFormat.parse(endDate)

        if (startDateParsed != null && endDateParsed != null) {
            firestoreRepository.getTimeEntriesByDateRange(username, startDateParsed, endDateParsed) { entries ->
                val categoryHours = HashMap<String, Double>()

                // Calculate hours spent on each category
                for (entry in entries) {
                    val hours = entry.hours ?: 0.0
                    categoryHours[entry.category] = categoryHours.getOrDefault(entry.category, 0.0) + hours
                }

                // Update the UI with the calculated hours
                updateCategorySummaryUI(categoryHours)
            }
        }
    }

    private fun updateCategorySummaryUI(categoryHoursMap: HashMap<String, Double>) {
        categoryHours.clear()
        categoryHours.addAll(categoryHoursMap.entries.map { it.toPair() })
        categoryHoursAdapter.notifyDataSetChanged()
    }

    inner class CategoryHoursAdapter(private val categoryHours: List<Pair<String, Double>>) : BaseAdapter() {
        override fun getCount(): Int {
            return categoryHours.size
        }

        override fun getItem(position: Int): Any {
            return categoryHours[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)

            val categoryTextView = view.findViewById<TextView>(android.R.id.text1)
            val hoursTextView = view.findViewById<TextView>(android.R.id.text2)

            val (category, hours) = getItem(position) as Pair<String, Double>
            categoryTextView.text = category
            hoursTextView.text = "$hours hours"

            return view
        }
    }

    private fun loadDailyHoursChart(startDate: String, endDate: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val startDateParsed = dateFormat.parse(startDate)
        val endDateParsed = dateFormat.parse(endDate)

        if (startDateParsed != null && endDateParsed != null) {
            firestoreRepository.getTimeEntriesByDateRange(username, startDateParsed, endDateParsed) { entries ->
                firestoreRepository.getGoalsData(username) { _, minGoal, maxGoal ->
                    val dailyHours = mutableMapOf<String, Double>()

                    // Calculate hours worked each day
                    for (entry in entries) {
                        val hours = entry.hours ?: 0.0
                        dailyHours[entry.date] = dailyHours.getOrDefault(entry.date, 0.0) + hours
                    }

                    // Prepare data for the chart
                    val data: MutableList<DataEntry> = dailyHours.map { ValueDataEntry(it.key, it.value) }.toMutableList()

                    // Initialize AnyChartView here
                    val anyChartView = findViewById<AnyChartView>(R.id.any_chart_view)
                    val cartesian: Cartesian = AnyChart.column()

                    val column: Column = cartesian.column(data)

                    cartesian.animation(true)
                    cartesian.title("Daily Hours Worked")

                    column.tooltip()
                        .titleFormat("{%X}")
                        .position(Position.CENTER_BOTTOM)
                        .anchor(Anchor.CENTER_BOTTOM)
                        .offsetX(0.0)
                        .offsetY(5.0)
                        .format("{%Value}{groupsSeparator: } hours")

                    cartesian.yScale().minimum(0.0)
                    cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: } hours")
                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT)
                    cartesian.interactivity().hoverMode(HoverMode.BY_X)
                    cartesian.xAxis(0).title("Date")
                    cartesian.yAxis(0).title("Hours Worked")

                    // Add markers for min and max goals
                    val minGoalMarker = cartesian.lineMarker(0)
                    minGoalMarker.value(minGoal)
                    minGoalMarker.stroke("3 #00FF00")

                    val maxGoalMarker = cartesian.lineMarker(1)
                    maxGoalMarker.value(maxGoal)
                    maxGoalMarker.stroke("3 #FF0000")

                    // Adjust y-axis range to fit the data
                    val yAxis = cartesian.yScale()
                    val maxYValue = maxOf(maxGoal, data.maxOfOrNull { (it as ValueDataEntry).getValue("value") as? Double ?: 0.0 } ?: 0.0)
                    yAxis.maximum(maxYValue + 1) // Adding a little padding above the maximum value

                    // Set x-axis scale to match the filtered date range
                    /*val ordinalScale = Ordinal.instantiate()
                    cartesian.xScale(ordinalScale)

                    // Set the x-axis range to match the filtered date range
                    val startDateNumeric = startDateParsed.time.toDouble()
                    val endDateNumeric = endDateParsed.time.toDouble()
                    ordinalScale.set("minimum", startDateNumeric)
                    ordinalScale.set("maximum", endDateNumeric)*/

                    // Use dateTime scale for x-axis
                    val dateTimeScale = DateTime.instantiate()
                    cartesian.xScale(dateTimeScale)

                    // Set the x-axis range to match the filtered date range
                    dateTimeScale.minimum(startDateParsed.time)
                    dateTimeScale.maximum(endDateParsed.time)

                    // Set the new chart instance to the AnyChartView
                    anyChartView.setChart(cartesian)
                    anyChartView.invalidate()
                }
            }
        }
    }
}

