package com.example.madlab.experiment8

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.madlab.experiment8.databinding.ItemCourseListBinding

class CourseAdapter(
    context: Context,
    private val courses: List<CourseItem>
) : ArrayAdapter<CourseItem>(context, 0, courses) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemCourseListBinding
        val view: View

        if (convertView == null) {
            binding = ItemCourseListBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemCourseListBinding
        }

        val item = getItem(position)
        item?.let {
            binding.tvCourseTitle.text = it.title
            binding.tvCourseCode.text = "Code: ${it.code}"
            binding.tvCourseStatus.text = it.credits
            binding.ivCourseIcon.setImageResource(it.imageResId)
        }

        return view
    }
}
