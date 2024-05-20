package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val firestoreRepository = FirestoreRepository(this)
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        //Register link click listener
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            // Disable the button to prevent multiple clicks
            binding.btnRegister.isEnabled = false

            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            // Check if the fields are not empty
            if (username.isNotBlank() && password.isNotBlank()) {
                // Call the registerUser method and handle the registration logic
                registerUser(username, password)
            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
                // Re-enable the button if the fields are empty
                binding.btnRegister.isEnabled = true
            }
        }
    }

    private fun registerUser(username: String, password: String) {
        firestoreRepository.userExists(username) { exists ->
            if (!exists) {
                val newUser = User(username, password)
                firestoreRepository.addUser(newUser) { success ->
                    runOnUiThread {
                        if (success) {
                            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                            finish() // Closes the activity and returns to the previous screen (MainActivity)
                        } else {
                            Toast.makeText(this, "Failed to register. Please try again later.", Toast.LENGTH_SHORT).show()
                            // Re-enable the button if registration failed
                            binding.btnRegister.isEnabled = true
                        }
                    }
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this, "Username already taken, choose another!", Toast.LENGTH_SHORT).show()
                    // Re-enable the button if the username is taken
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

}