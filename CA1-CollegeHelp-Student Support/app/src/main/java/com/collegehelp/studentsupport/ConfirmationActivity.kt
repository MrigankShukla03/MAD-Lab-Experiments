package com.collegehelp.studentsupport

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.collegehelp.studentsupport.databinding.ActivityConfirmationBinding
import kotlin.random.Random

class ConfirmationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmationBinding

    private var studentName: String = ""
    private var category: String = ""
    private var problemDesc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "ConfirmationActivity: onCreate")

        binding = ActivityConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        studentName = intent.getStringExtra(EXTRA_STUDENT_NAME) ?: "Student"
        category = intent.getStringExtra(EXTRA_CATEGORY) ?: "Support Request"
        problemDesc = intent.getStringExtra(EXTRA_PROBLEM_DESC) ?: "N/A"

        setupUI()
        triggerNotification()

        binding.btnReturnHome.setOnClickListener {
            Log.d(TAG, "ConfirmationActivity: Return Home button clicked")
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setupUI() {
        binding.tvStudentName.text = studentName
        binding.tvCategory.text = category
        binding.tvProblemDescription.text = problemDesc

        val randomTicketNum = Random.nextInt(1000, 9999)
        binding.tvTicketId.text = "#REQ-$randomTicketNum"
    }

    private fun triggerNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                )
            } else {
                NotificationHelper.showSubmissionNotification(this, studentName, category)
            }
        } else {
            NotificationHelper.showSubmissionNotification(this, studentName, category)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationHelper.showSubmissionNotification(this, studentName, category)
            } else {
                Log.d(TAG, "ConfirmationActivity: POST_NOTIFICATIONS permission denied by user")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "ConfirmationActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "ConfirmationActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "ConfirmationActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "ConfirmationActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ConfirmationActivity: onDestroy")
    }

    companion object {
        private const val TAG = "CollegeHelp"
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101

        const val EXTRA_STUDENT_NAME = "extra_student_name"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_PROBLEM_DESC = "extra_problem_desc"
    }
}
