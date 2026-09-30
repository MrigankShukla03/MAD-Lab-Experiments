package com.example.madlab.experiment8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment8.databinding.FragmentStudentDetailsBinding

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

        // 1. Button Listener
        binding.demoButton.setOnClickListener {
            val input = binding.demoEditText.text.toString()
            val message = if (input.isNotEmpty()) "Input: $input" else "Standard Button Clicked"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // 2. ImageButton Listener
        binding.demoImageButton.setOnClickListener {
            Toast.makeText(context, "Camera Icon Clicked!", Toast.LENGTH_SHORT).show()
        }

        // 3. CheckBox Listener
        binding.demoCheckBox.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Accepted" else "Declined"
            Toast.makeText(context, "Terms $status", Toast.LENGTH_SHORT).show()
        }

        // 4. ToggleButton Listener
        binding.demoToggleButton.setOnCheckedChangeListener { _, isChecked ->
            val state = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(context, "Feature $state", Toast.LENGTH_SHORT).show()
        }

        // 5. RadioGroup Listener
        binding.demoRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedOption = when (checkedId) {
                R.id.radioOption1 -> "Option A"
                R.id.radioOption2 -> "Option B"
                else -> "None"
            }
            Toast.makeText(context, "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
