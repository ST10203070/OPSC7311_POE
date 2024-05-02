package com.example.opsc7311_poe
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityGoalsBinding

class GoalsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGoalsBinding
    private lateinit var firestoreRepository: FirestoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirestoreRepository
        firestoreRepository = FirestoreRepository(this )

        setupGoalSetting()
        setupNavigation()
    }

    private fun setupGoalSetting() {
        binding.btnSetGoal.setOnClickListener {
            val minGoal = binding.etMinGoal.text.toString().toDoubleOrNull()
            val maxGoal = binding.etMaxGoal.text.toString().toDoubleOrNull()

            if (minGoal == null || maxGoal == null || minGoal >= maxGoal) {
                Toast.makeText(this, "Please enter valid goals (min < max)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Correct return statement within a lambda
            }

            // Continue with the rest of the code if the input is valid
            val userId = "user123"  // Example user ID
            // Assume you have a method in FirestoreRepository to handle goal saving
            firestoreRepository.saveUserGoals(userId, minGoal!!, maxGoal!!, {
                Toast.makeText(this, "Goals saved successfully", Toast.LENGTH_SHORT).show()
            }, { exception ->
                Toast.makeText(this, "Failed to save goals: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun setupNavigation() {
        binding.btnNewTimeEntry.setOnClickListener {
            // Navigate to New Time Entry Activity
            val intent = Intent(this, TimeEntryActivity::class.java)
            startActivity(intent)
        }

        binding.btnDataVisualization.setOnClickListener {
            // Navigate to Data Visualization Activity
            val intent = Intent(this, DataVisualisationActivity::class.java)
            startActivity(intent)
        }
    }
}


//NYASHA
//Implement goal setting functionality, including UI and logic for setting minimum and maximum daily goals for hours worked.