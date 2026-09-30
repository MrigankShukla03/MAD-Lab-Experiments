package com.example.madlab.experiment8

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab.experiment8.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val usn = binding.usnEditText.text.toString().trim()

            if (name.isEmpty() || usn.isEmpty()) {
                Toast.makeText(this, "Please enter Name and USN", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    putExtra("USER_NAME", name)
                    putExtra("USER_USN", usn)
                }
                startActivity(intent)
                finish()
            }
        }
    }
}
