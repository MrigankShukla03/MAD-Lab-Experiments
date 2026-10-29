package com.example.madlab.experiment12

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.madlab.experiment12.databinding.ItemApiPostBinding

class ApiPostAdapter(
    context: Context,
    private val posts: List<ApiPost>
) : ArrayAdapter<ApiPost>(context, 0, posts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemApiPostBinding
        val view: View

        if (convertView == null) {
            binding = ItemApiPostBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemApiPostBinding
        }

        val item = getItem(position)
        item?.let {
            binding.tvApiTitle.text = "[#${it.id}] ${it.title}"
            binding.tvApiBody.text = it.body
        }

        return view
    }
}
