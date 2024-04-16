package com.example.opsc7311_poe

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc7311_poe.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

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
        return true
    }
}