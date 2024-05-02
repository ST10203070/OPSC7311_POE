package com.example.opsc7311_poe

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityTimeEntryBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class TimeEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeEntryBinding
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up click listeners for EditText fields to show time picker dialog
        binding.etStartTime.setOnClickListener {
            showTimePicker(binding.etStartTime)
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker(binding.etEndTime)
        }
        // Set up date picker dialog for etDate EditText
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Set up click listener for btnSaveTimeEntry button
        binding.btnSaveTimeEntry.setOnClickListener {
            saveTimeEntry()
        }

        // Set up click listener for btnAddPhoto button
        binding.btnAddPhoto.setOnClickListener {
            openGallery()
        }
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

    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                editText.setText(time)
            },
            hourOfDay,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun saveTimeEntry() {
        // Retrieve data from EditText fields
        val date = binding.etDate.text.toString()
        val startTime = binding.etStartTime.text.toString()
        val endTime = binding.etEndTime.text.toString()
        val description = binding.etDescription.text.toString()
        val category = binding.etCategory.text.toString()

        // Validate data (you can add your validation logic here)

        // Save time entry to Firestore
        val timeEntry = hashMapOf(
            "date" to date,
            "startTime" to startTime,
            "endTime" to endTime,
            "description" to description,
            "category" to category
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
        }
    }

    companion object {
        const val REQUEST_IMAGE_GALLERY = 1001
    }
}

//NYASHA
//Implement category management functionality, including UI and logic for creating categories.

//CAM
//Implement the functionality for users to create new timesheet entries, specifying the date, start and end times, description, and category.
//-Implement the optional feature to add a photograph to each timesheet entry.