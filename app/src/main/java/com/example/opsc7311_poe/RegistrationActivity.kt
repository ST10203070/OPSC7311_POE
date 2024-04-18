package com.example.opsc7311_poe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityRegistrationBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val firestoreRepository = FirestoreRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            if (registerUser(binding.etUsername.text.toString(), binding.etPassword.text.toString())) {
                finish() // Closes the activity and returns to the previous screen (MainActivity)
            } else {
                binding.tvStatus.text = "Registration failed, try a different username!"
            }
        }
    }

    private fun registerUser(username: String, password: String): Boolean {
        // Placeholder for registration logic

        //Logic to add user to User table through FirestoreRepository class
        /*val user = User("exampleUsername", "examplePassword", 4.5, 8.0)
        firestoreRepository.addUser(user)*/

        return true
    }
}