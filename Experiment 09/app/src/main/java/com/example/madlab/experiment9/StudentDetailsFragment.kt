package com.example.madlab.experiment9

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment9.databinding.FragmentStudentDetailsBinding

class StudentDetailsFragment : Fragment() {

    private var _binding: FragmentStudentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var prefManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DatabaseHelper(requireContext())
        prefManager = PreferenceManager(requireContext())

        // 1. SharedPreferences Key-Value Session
        val currentName = prefManager.userName.ifEmpty { "Mrigank Shukla" }
        val currentUsn = prefManager.userUsn.ifEmpty { "25MCAR0109" }
        binding.tvPrefSessionInfo.text = "Logged in as: $currentName ($currentUsn)"

        // Pre-fill inputs with logged in user data
        binding.etSqliteName.setText(currentName)
        binding.etSqliteUsn.setText(currentUsn)
        binding.etSqliteCourse.setText("Mobile Application Development")

        // Seed initial sample SQLite data if empty
        if (dbHelper.getAllRecords().isEmpty()) {
            dbHelper.insertRecord("Mrigank Shukla", "25MCAR0109", "Mobile App Development")
            dbHelper.insertRecord("Alex Johnson", "25MCAR0042", "Database Systems")
        }

        refreshRecords()

        // 2. SQLite CREATE
        binding.btnAddRecord.setOnClickListener {
            val name = binding.etSqliteName.text.toString().trim()
            val usn = binding.etSqliteUsn.text.toString().trim()
            val course = binding.etSqliteCourse.text.toString().trim()

            if (name.isEmpty() || usn.isEmpty() || course.isEmpty()) {
                Toast.makeText(context, "Please enter Name, USN, and Course", Toast.LENGTH_SHORT).show()
            } else {
                val id = dbHelper.insertRecord(name, usn, course)
                if (id != -1L) {
                    Toast.makeText(context, "Record added to SQLite DB!", Toast.LENGTH_SHORT).show()
                    binding.etSqliteCourse.text?.clear()
                    refreshRecords()
                } else {
                    Toast.makeText(context, "Failed to add record", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 3. SQLite DELETE ALL
        binding.btnClearDatabase.setOnClickListener {
            dbHelper.deleteAllRecords()
            Toast.makeText(context, "All SQLite records deleted", Toast.LENGTH_SHORT).show()
            refreshRecords()
        }
    }

    fun refreshRecords() {
        val records = dbHelper.getAllRecords()
        val adapter = SqliteRecordAdapter(requireContext(), records) { recordToDelete ->
            dbHelper.deleteRecord(recordToDelete.id)
            Toast.makeText(context, "Deleted record ID #${recordToDelete.id}", Toast.LENGTH_SHORT).show()
            refreshRecords()
        }
        binding.lvSqliteRecords.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
