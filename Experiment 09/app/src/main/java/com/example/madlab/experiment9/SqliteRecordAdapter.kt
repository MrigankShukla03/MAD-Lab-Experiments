package com.example.madlab.experiment9

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.madlab.experiment9.databinding.ItemSqliteRecordBinding

class SqliteRecordAdapter(
    context: Context,
    private val records: List<StudentRecord>,
    private val onDeleteClicked: (StudentRecord) -> Unit
) : ArrayAdapter<StudentRecord>(context, 0, records) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemSqliteRecordBinding
        val view: View

        if (convertView == null) {
            binding = ItemSqliteRecordBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as ItemSqliteRecordBinding
        }

        val item = getItem(position)
        item?.let { record ->
            binding.tvSqliteTitle.text = "${record.name} (${record.usn})"
            binding.tvSqliteSubtitle.text = "Course: ${record.course}"

            binding.btnDeleteRecord.setOnClickListener {
                onDeleteClicked(record)
            }
        }

        return view
    }
}
