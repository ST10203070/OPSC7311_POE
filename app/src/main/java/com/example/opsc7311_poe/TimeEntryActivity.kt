package com.example.opsc7311_poe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityTimeEntryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimeEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeEntryBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var selectedCategoryId: Int = -1
    private var selectedPhotoPath: String? = null
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var startTimeInMillis: Long = 0
    private var endTimeInMillis: Long = 0
    private val categories = ArrayList<String>()
    private val fireStoreRepository = FirestoreRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the date to the current date
        val currentDate = dateFormat.format(Date())
        binding.etDate.text = currentDate

        // Set up date picker button click listeners
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        // Set up click listeners for EditText fields to show time picker dialog
        binding.etStartTime.setOnClickListener {
            showTimePicker(binding.etStartTime)
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker(binding.etEndTime)
        }

        // Set up duration calculation
        binding.etStartTime.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                calculateDuration()
            }
        }
        binding.etEndTime.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                calculateDuration()
            }
        }

        // Set up duration adjustment buttons
        binding.btnAdd30.setOnClickListener {
            adjustDuration(30)
        }
        binding.btnAdd60.setOnClickListener {
            adjustDuration(60)
        }
        binding.btnAdd480.setOnClickListener {
            adjustDuration(480)
        }

        // Set up click listener for btnSaveTimeEntry button
        binding.btnSaveTimeEntry.setOnClickListener {
            saveTimeEntry()
        }

        // Set up click listener for btnAddPhoto button
        binding.btnAddPhoto.setOnClickListener {
            openGallery()
        }

        // Set goals click listener
        binding.btnDailyGoals.setOnClickListener {
            startActivity(Intent(this, GoalsActivity::class.java))
        }

        // Data visualisation click listener
        binding.btnDataVisualisation.setOnClickListener {
            startActivity(Intent(this, DataVisualisationActivity::class.java))
        }

        // Set up category add button
        binding.btnAddCategory.setOnClickListener {
            addCategory()
        }

        // Set up Spinner for categories
        setupCategorySpinner()

        // Set up Spinner for photos
        setupPhotoSpinner()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            binding.etDate.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth))
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker(editText: TextView) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                editText.setText(time)
                updateTimesInMillis()
                calculateDuration()
            },
            hourOfDay,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun updateTimesInMillis() {
        val startTimeText = binding.etStartTime.text.toString()
        val endTimeText = binding.etEndTime.text.toString()
        if (startTimeText.isNotEmpty() && endTimeText.isNotEmpty()) {
            startTimeInMillis = timeFormat.parse(startTimeText)?.time ?: 0
            endTimeInMillis = timeFormat.parse(endTimeText)?.time ?: 0
        }
    }

    private fun calculateDuration() {
        if (startTimeInMillis != 0L && endTimeInMillis != 0L) {
            val durationInMillis = endTimeInMillis - startTimeInMillis
            val durationInMinutes = durationInMillis / (1000 * 60)
            val hours = durationInMinutes / 60
            val minutes = durationInMinutes % 60

            binding.etDuration.text = if (hours > 0) {
                String.format("%d hours %d minutes", hours, minutes)
            } else {
                String.format("%d minutes", minutes)
            }
        }
    }

    private fun adjustDuration(seconds: Int) {
        if (startTimeInMillis != 0L) {
            endTimeInMillis += seconds * 1000
            binding.etEndTime.text = timeFormat.format(Date(endTimeInMillis))
            calculateDuration()
        }
    }

    private fun saveTimeEntry() {
        // Retrieve data from EditText fields
        val date = binding.etDate.text.toString()
        val startTime = binding.etStartTime.text.toString()
        val endTime = binding.etEndTime.text.toString()
        val description = binding.etDescription.text.toString()
        val note = binding.etNote.text.toString()
        val category = binding.spinnerCategory.selectedItem?.toString() ?: ""
        val photo = binding.spinnerAddPhoto.selectedItem?.toString() ?: ""

        // Validate data (you can add your validation logic here)

        // Save time entry to Firestore
        val timeEntry = hashMapOf(
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "description" to description,
            "note" to note,
            "category" to category,
            "photo" to photo
        )

        firestore.collection("time_entries")
            .add(timeEntry)
            .addOnSuccessListener {
                Toast.makeText(this, "Time entry saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Close this activity after saving
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving time entry: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            // You can handle the selected image URI here
            selectedPhotoPath = selectedImageUri.toString()
        }
    }

    private fun setupCategorySpinner() {
        val userId = getCurrentUserId()
        fireStoreRepository.getCategories(userId) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = adapter

            binding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedCategoryId = categories[position].id
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCategoryId = -1
                }
            }
        }
    }

    private fun setupPhotoSpinner() {
        // Fetch photos from Firestore or other source
        val photos = listOf("Photo 1", "Photo 2", "Photo 3")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, photos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAddPhoto.adapter = adapter

        binding.spinnerAddPhoto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPhotoPath = photos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedPhotoPath = null
            }
        }
    }

    private fun addCategory() {
        val newCategory = binding.etNewCategory.text.toString()
        if (newCategory.isNotEmpty()) {
            val userId = getCurrentUserId()
            val category = Category(id = generateCategoryId(), name = newCategory) // Ensure to generate a unique ID
            fireStoreRepository.addCategory(userId, category) {
                binding.etNewCategory.text.clear()
                setupCategorySpinner()
                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Category name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentUserId(): String {
        // Implement this method to return the current signed-in user's ID
        // For example, using FirebaseAuth:
        // return FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return "dummyUserId" // Replace with actual user ID retrieval logic
    }

    private fun generateCategoryId(): Int {
        // Implement a method to generate a unique category ID
        // For simplicity, this can be a timestamp or a combination of timestamp and user ID
        return (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
    }

    companion object {
        const val REQUEST_IMAGE_GALLERY = 1001
    }
}
