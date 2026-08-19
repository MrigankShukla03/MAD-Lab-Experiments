package com.example.madlab.experiment4

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab.experiment4.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val usn = binding.usnEditText.text.toString()

            if (name.isNotEmpty() && usn.isNotEmpty()) {
                // Explicit Intent to DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java).apply {
                    putExtra("USER_NAME", name)
                    putExtra("USER_USN", usn)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter your details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
