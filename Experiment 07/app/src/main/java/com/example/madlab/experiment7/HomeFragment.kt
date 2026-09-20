package com.example.madlab.experiment7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment7.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseList = listOf(
            CourseItem(
                title = "Mobile Application Development",
                code = "25MCA201",
                description = "Comprehensive Android App Development with Kotlin, Views & Material 3 UI.",
                imageResId = R.drawable.ic_course_mobile,
                credits = "4 Credits"
            ),
            CourseItem(
                title = "Java & Object Oriented Programming",
                code = "25MCA202",
                description = "Advanced OOP concepts, multithreading, collections framework, and design patterns.",
                imageResId = R.drawable.ic_course_java,
                credits = "4 Credits"
            ),
            CourseItem(
                title = "Database Management Systems",
                code = "25MCA203",
                description = "Relational database design, SQL querying, indexing, and transaction processing.",
                imageResId = R.drawable.ic_course_database,
                credits = "3 Credits"
            ),
            CourseItem(
                title = "Web Technologies & Frameworks",
                code = "25MCA204",
                description = "Responsive web development, HTML5, CSS3, JavaScript, and backend integration.",
                imageResId = R.drawable.ic_course_web,
                credits = "3 Credits"
            )
        )

        val adapter = CourseAdapter(requireContext(), courseList)
        binding.lvCourses.adapter = adapter

        // Initial preview state
        if (courseList.isNotEmpty()) {
            updatePreview(courseList[0])
        }

        // ListView item click listener
        binding.lvCourses.setOnItemClickListener { _, _, position, _ ->
            val selectedCourse = courseList[position]
            updatePreview(selectedCourse)
            Toast.makeText(context, "Selected: ${selectedCourse.title}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePreview(course: CourseItem) {
        binding.tvDetailTitle.text = course.title
        binding.tvDetailDescription.text = course.description
        binding.ivDetailImage.setImageResource(course.imageResId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
