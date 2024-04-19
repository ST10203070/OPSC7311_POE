package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.opsc7311_poe.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.opsc7311_poe.ui.theme.OPSC7311_POETheme
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Login button click listener
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            // Basic input validation
            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter both username and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate login with Firestore
            validateLogin(username, password)
        }

        //Register link click listener
        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    //Method to validate login
    private fun validateLogin(username: String, password: String) {
        firestoreRepository.getUser(username) { user ->
            if (user != null && user.password == password) {
                // Password matches, proceed to login
                runOnUiThread {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    //Redirecting to Time Entry screen
                    startActivity(Intent(this, TimeEntryActivity::class.java))
                }
            } else {
                // User doesn't exist or password doesn't match
                runOnUiThread {
                    Toast.makeText(this, "Invalid credentials, try again!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}

//MAX
//Implement registration and login