package com.example.madlab.experiment8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.madlab.experiment8.databinding.FragmentHomeBinding

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
                credits = "4 Credits",
                webUrl = "https://developer.android.com/courses"
            ),
            CourseItem(
                title = "Java & Object Oriented Programming",
                code = "25MCA202",
                description = "Advanced OOP concepts, multithreading, collections framework, and design patterns.",
                imageResId = R.drawable.ic_course_java,
                credits = "4 Credits",
                webUrl = "https://docs.oracle.com/en/java/"
            ),
            CourseItem(
                title = "Database Management Systems",
                code = "25MCA203",
                description = "Relational database design, SQL querying, indexing, and transaction processing.",
                imageResId = R.drawable.ic_course_database,
                credits = "3 Credits",
                webUrl = "https://www.sqlite.org/docs.html"
            ),
            CourseItem(
                title = "Web Technologies & Frameworks",
                code = "25MCA204",
                description = "Responsive web development, HTML5, CSS3, JavaScript, and backend integration.",
                imageResId = R.drawable.ic_course_web,
                credits = "3 Credits",
                webUrl = "https://developer.mozilla.org"
            )
        )

        val adapter = CourseAdapter(requireContext(), courseList)
        binding.lvCourses.adapter = adapter

        if (courseList.isNotEmpty()) {
            updatePreview(courseList[0])
        }

        // Item Click Listener with PopupMenu demonstration
        binding.lvCourses.setOnItemClickListener { parent, itemView, position, _ ->
            val selectedCourse = courseList[position]
            updatePreview(selectedCourse)
            showCoursePopupMenu(itemView, selectedCourse)
        }
    }

    private fun updatePreview(course: CourseItem) {
        binding.tvDetailTitle.text = course.title
        binding.tvDetailDescription.text = course.description
        binding.ivDetailImage.setImageResource(course.imageResId)
    }

    private fun showCoursePopupMenu(anchorView: View, course: CourseItem) {
        val popup = PopupMenu(requireContext(), anchorView)
        popup.menuInflater.inflate(R.menu.context_popup_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popup_open_portal -> {
                    Toast.makeText(context, "Opening Web Portal for ${course.title}", Toast.LENGTH_SHORT).show()
                    (activity as? DashboardActivity)?.openWebPortal(course.webUrl)
                    true
                }
                R.id.popup_share -> {
                    Toast.makeText(context, "Sharing Course: ${course.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.popup_bookmark -> {
                    Toast.makeText(context, "Bookmarked: ${course.title}", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
