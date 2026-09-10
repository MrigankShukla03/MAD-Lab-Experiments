package com.collegehelp.studentsupport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.collegehelp.studentsupport.databinding.FragmentSupportCategoryBinding
import com.collegehelp.studentsupport.models.SupportCategoryType

class SupportCategoryFragment : Fragment() {

    private var _binding: FragmentSupportCategoryBinding? = null
    private val binding get() = _binding!!

    private var selectedCategory: SupportCategoryType = SupportCategoryType.ACADEMIC

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "SupportCategoryFragment: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SupportCategoryFragment: onCreate")

        arguments?.let {
            val categoryOrdinal = it.getInt(ARG_CATEGORY_ORDINAL, SupportCategoryType.ACADEMIC.ordinal)
            val categories = SupportCategoryType.values()
            selectedCategory = if (categoryOrdinal in categories.indices) {
                categories[categoryOrdinal]
            } else {
                SupportCategoryType.ACADEMIC
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SupportCategoryFragment: onCreateView")
        _binding = FragmentSupportCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "SupportCategoryFragment: onViewCreated")

        updateCategoryUI()

        binding.btnSubmitRequest.setOnClickListener {
            submitForm()
        }
    }

    private fun updateCategoryUI() {
        val categoryTitle = getString(selectedCategory.titleResId)
        binding.tvCategoryTitle.text = categoryTitle
        binding.tvCategoryDescription.text = getString(selectedCategory.infoResId)
        binding.ivCategoryIcon.setImageResource(selectedCategory.iconResId)
        binding.containerCategoryHeader.setBackgroundColor(
            ContextCompat.getColor(requireContext(), selectedCategory.bgColorResId)
        )
    }

    private fun submitForm() {
        val studentName = binding.etStudentName.text.toString().trim()
        val problemDesc = binding.etProblemDescription.text.toString().trim()

        var isValid = true

        if (studentName.isEmpty()) {
            binding.tilStudentName.error = getString(R.string.error_empty_name)
            isValid = false
        } else {
            binding.tilStudentName.error = null
        }

        if (problemDesc.isEmpty()) {
            binding.tilProblemDescription.error = getString(R.string.error_empty_description)
            isValid = false
        } else {
            binding.tilProblemDescription.error = null
        }

        if (isValid) {
            val categoryTitle = getString(selectedCategory.titleResId)
            Log.d(TAG, "SupportCategoryFragment: Form submitted by $studentName for $categoryTitle")

            // Activity-Activity Navigation via Intent
            val intent = Intent(requireContext(), ConfirmationActivity::class.java).apply {
                putExtra(ConfirmationActivity.EXTRA_STUDENT_NAME, studentName)
                putExtra(ConfirmationActivity.EXTRA_CATEGORY, categoryTitle)
                putExtra(ConfirmationActivity.EXTRA_PROBLEM_DESC, problemDesc)
            }
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "SupportCategoryFragment: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "SupportCategoryFragment: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "SupportCategoryFragment: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "SupportCategoryFragment: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "SupportCategoryFragment: onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SupportCategoryFragment: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "SupportCategoryFragment: onDetach")
    }

    companion object {
        private const val TAG = "CollegeHelp"
        private const val ARG_CATEGORY_ORDINAL = "arg_category_ordinal"

        fun newInstance(categoryType: SupportCategoryType): SupportCategoryFragment {
            val fragment = SupportCategoryFragment()
            val args = Bundle().apply {
                putInt(ARG_CATEGORY_ORDINAL, categoryType.ordinal)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
