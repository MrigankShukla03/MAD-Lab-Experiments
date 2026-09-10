package com.collegehelp.studentsupport

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.collegehelp.studentsupport.databinding.ActivityHomeBinding
import com.collegehelp.studentsupport.models.SupportCategoryType

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var currentCategory: SupportCategoryType = SupportCategoryType.ACADEMIC

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "HomeActivity: onCreate")

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryCardListeners()

        if (savedInstanceState == null) {
            selectCategory(SupportCategoryType.ACADEMIC)
        }
    }

    private fun setupCategoryCardListeners() {
        binding.cardAcademic.setOnClickListener {
            selectCategory(SupportCategoryType.ACADEMIC)
        }

        binding.cardTechnical.setOnClickListener {
            selectCategory(SupportCategoryType.TECHNICAL)
        }

        binding.cardLibrary.setOnClickListener {
            selectCategory(SupportCategoryType.LIBRARY)
        }
    }

    private fun selectCategory(categoryType: SupportCategoryType) {
        currentCategory = categoryType
        Log.d(TAG, "HomeActivity: Category selected -> ${categoryType.name}")

        updateCardSelectionStates(categoryType)

        // Activity to Fragment Communication / Navigation
        val fragment = SupportCategoryFragment.newInstance(categoryType)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateCardSelectionStates(selected: SupportCategoryType) {
        val selectedStrokeColor = ContextCompat.getColor(this, R.color.card_stroke_selected)
        val defaultStrokeColor = ContextCompat.getColor(this, R.color.card_stroke_default)

        val selectedStrokeWidth = resources.getDimensionPixelSize(R.dimen.card_selected_stroke_width)
        val defaultStrokeWidth = resources.getDimensionPixelSize(R.dimen.card_default_stroke_width)

        // Reset Academic Card
        binding.cardAcademic.strokeColor = if (selected == SupportCategoryType.ACADEMIC) selectedStrokeColor else defaultStrokeColor
        binding.cardAcademic.strokeWidth = if (selected == SupportCategoryType.ACADEMIC) selectedStrokeWidth else defaultStrokeWidth

        // Reset Technical Card
        binding.cardTechnical.strokeColor = if (selected == SupportCategoryType.TECHNICAL) selectedStrokeColor else defaultStrokeColor
        binding.cardTechnical.strokeWidth = if (selected == SupportCategoryType.TECHNICAL) selectedStrokeWidth else defaultStrokeWidth

        // Reset Library Card
        binding.cardLibrary.strokeColor = if (selected == SupportCategoryType.LIBRARY) selectedStrokeColor else defaultStrokeColor
        binding.cardLibrary.strokeWidth = if (selected == SupportCategoryType.LIBRARY) selectedStrokeWidth else defaultStrokeWidth
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "HomeActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "HomeActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "HomeActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "HomeActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "HomeActivity: onDestroy")
    }

    companion object {
        private const val TAG = "CollegeHelp"
    }
}
