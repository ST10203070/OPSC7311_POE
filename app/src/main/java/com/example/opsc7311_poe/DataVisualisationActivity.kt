package com.example.opsc7311_poe

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.opsc7311_poe.databinding.ActivityDataVisualisationBinding
import java.text.SimpleDateFormat
import java.util.*

class DataVisualisationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataVisualisationBinding
    private lateinit var timesheetEntriesAdapter: TimesheetEntriesAdapter
    private val timesheetEntries = mutableListOf<TimeEntry>()
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
        Log.d("DataVisualisation", "Photo URI: $uri")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Unable to open photo", Toast.LENGTH_SHORT).show()
            Log.e("DataVisualisation", "Error opening photo: ${e.message}")
        }
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
        val dialog = AlertDialog.Builder(this)
        val imageView = ImageView(this)
        imageView.setImageURI(uri)
        dialog.setView(imageView)
        dialog.setPositiveButton("Close") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialog.show()
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
        // Add dummy data for now
        binding.category1.text = "Category 1      2"
        binding.category2.text = "Category 2      3"
        binding.category3.text = "Category 3      4"

        // Update chart placeholder
        binding.chartImageView.setImageResource(R.drawable.logo/*ic_chart_placeholder*/)
    }
}

