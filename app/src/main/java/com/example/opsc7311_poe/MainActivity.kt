package com.example.opsc7311_poe

import android.content.Intent
import android.os.Bundle
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

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            if (validateLogin(binding.etUsername.text.toString(), binding.etPassword.text.toString())) {
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                binding.tvStatus.text = "Invalid credentials, try again!"
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // Placeholder for actual validation logic
        return username == "admin" && password == "admin"
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OPSC7311_POETheme {
        Greeting("Android")
    }
}