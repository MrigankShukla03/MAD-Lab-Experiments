package com.example.madlab.experiment9

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment9.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PreferenceManager(requireContext())

        val name = prefManager.userName.ifEmpty { "Mrigank Shukla" }
        val usn = prefManager.userUsn.ifEmpty { "25MCAR0109" }

        binding.tvAccountName.text = name
        binding.tvAccountUsn.text = "USN: $usn"

        binding.switchNotifications.isChecked = prefManager.notificationsEnabled
        binding.switchDarkMode.isChecked = prefManager.darkModeEnabled

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefManager.notificationsEnabled = isChecked
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(context, "SharedPreferences updated: Notifications $status", Toast.LENGTH_SHORT).show()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefManager.darkModeEnabled = isChecked
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(context, "SharedPreferences updated: Dark Theme $status", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            prefManager.clearSession()
            Toast.makeText(context, "SharedPreferences Cleared. Logged out.", Toast.LENGTH_SHORT).show()

            val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
