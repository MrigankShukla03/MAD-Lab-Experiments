package com.example.madlab.experiment11

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.madlab.experiment11.databinding.ItemContactRowBinding

class ContactAdapter(
    context: Context,
    private val contacts: List<ContactItem>
) : ArrayAdapter<ContactItem>(context, 0, contacts) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemContactRowBinding
        val view: View

        if (convertView == null) {
            binding = ItemContactRowBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemContactRowBinding
        }

        val item = getItem(position)
        item?.let {
            binding.tvContactName.text = it.name
            binding.tvContactPhone.text = it.phoneNumber
        }

        return view
    }
}
