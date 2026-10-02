package com.example.madlab.experiment9

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab.experiment9.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        // Check SharedPreferences for existing login session
        if (prefManager.isLoggedIn && prefManager.userName.isNotEmpty()) {
            openDashboard(prefManager.userName, prefManager.userUsn)
            return
        }

        binding.loginButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val usn = binding.usnEditText.text.toString().trim()

            if (name.isEmpty() || usn.isEmpty()) {
                Toast.makeText(this, "Please enter Name and USN", Toast.LENGTH_SHORT).show()
            } else {
                // Save session in SharedPreferences
                prefManager.saveSession(name, usn)
                openDashboard(name, usn)
            }
        }
    }

    private fun openDashboard(name: String, usn: String) {
        val intent = Intent(this, DashboardActivity::class.java).apply {
            putExtra("USER_NAME", name)
            putExtra("USER_USN", usn)
        }
        startActivity(intent)
        finish()
    }
}
