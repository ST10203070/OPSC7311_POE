package com.example.opsc7311_poe

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.opsc7311_poe.databinding.ActivityTimeEntryBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Calendar

class TimeEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimeEntryBinding
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimeEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                binding.etDate.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth))
            }, year, month, day).show()
        }

        binding.btnSave.setOnClickListener {
            saveTimeEntry()
        }
    }

    private fun saveTimeEntry() {
        // Logic to save the time entry
        finish() // Close this activity after saving
    }
}