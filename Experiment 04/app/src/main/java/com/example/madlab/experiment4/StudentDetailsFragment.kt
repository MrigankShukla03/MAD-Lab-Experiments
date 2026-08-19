package com.example.madlab.experiment4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.madlab.experiment4.databinding.FragmentStudentDetailsBinding

class StudentDetailsFragment : Fragment() {

    private var _binding: FragmentStudentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Mrigank Shukla"
        val usn = activity?.intent?.getStringExtra("USER_USN") ?: "25MCAR0109"

        binding.displayNameText.text = "Name: $name"
        binding.displayUsnText.text = "USN: $usn"

        // 1. Implicit Intent: Open Web Page
        binding.btnWeb.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MrigankShukla03"))
            startActivity(intent)
        }

        // 2. Implicit Intent: Dial Number
        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:250109"))
            startActivity(intent)
        }

        // 3. Implicit Intent: Share Text
        binding.btnShare.setOnClickListener {
            val shareText = "Student Details\nName: $name\nUSN: $usn\nExperiment 04: Android Intents"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Share Student Info via"))
        }

        // 4. Implicit Intent: View Map
        binding.btnMap.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:12.9716,77.5946?q=Bangalore"))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
