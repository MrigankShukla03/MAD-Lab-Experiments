package com.tripjournal.app

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tripjournal.app.databinding.ActivityFullScreenPhotoBinding

class FullScreenPhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUriStr = intent.getStringExtra("PHOTO_URI")
        if (!photoUriStr.isNullOrEmpty()) {
            try {
                binding.ivFullScreenPhoto.setImageURI(Uri.parse(photoUriStr))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.ivFullScreenPhoto.setOnClickListener {
            finish()
        }
    }
}
