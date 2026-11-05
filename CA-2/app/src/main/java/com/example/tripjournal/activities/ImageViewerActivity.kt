package com.example.tripjournal.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.tripjournal.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriStr = intent.getStringExtra("PHOTO_URI")
        if (!uriStr.isNullOrEmpty()) {
            try {
                binding.ivFullScreen.setImageURI(Uri.parse(uriStr))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.ivFullScreen.setOnClickListener {
            finish()
        }
    }
}
