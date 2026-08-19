package com.example.madlab.experiment5

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.madlab.experiment5.databinding.FragmentStudentDetailsBinding

class StudentDetailsFragment : Fragment() {

    private var _binding: FragmentStudentDetailsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var notificationHelper: NotificationHelper

    // Permission request launcher for Android 13+
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        notificationHelper = NotificationHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = activity?.intent?.getStringExtra("USER_NAME") ?: "Mrigank Shukla"
        val usn = activity?.intent?.getStringExtra("USER_USN") ?: "25MCAR0109"

        binding.displayNameText.text = "Name: $name"
        binding.displayUsnText.text = "USN: $usn"

        checkNotificationPermission()

        binding.btnSimpleNotify.setOnClickListener {
            notificationHelper.sendSimpleNotification(
                "Experiment 05: Success",
                "Name: $name | USN: $usn"
            )
        }

        binding.btnActionNotify.setOnClickListener {
            notificationHelper.sendActionNotification(
                "Action Required",
                "Click the button below to re-enter the app."
            )
        }

        binding.btnBigTextNotify.setOnClickListener {
            notificationHelper.sendBigTextNotification(
                "Android Activity Lifecycle & Fragments",
                "Expand to see details...",
                "This experiment focuses on notifications. By using fragments and material design, we create a flexible UI for $name ($usn)."
            )
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
