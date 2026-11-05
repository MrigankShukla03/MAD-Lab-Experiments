package com.example.tripjournal.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripjournal.databinding.ItemPhotoBinding
import com.example.tripjournal.models.Photo

class PhotoAdapter(
    private var photos: List<Photo>,
    private val onPhotoClicked: (Photo) -> Unit,
    private val onPhotoLongClicked: ((Photo) -> Unit)? = null
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        with(holder.binding) {
            try {
                ivPhotoThumb.setImageURI(Uri.parse(photo.photoUri))
            } catch (e: Exception) {
                ivPhotoThumb.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            root.setOnClickListener { onPhotoClicked(photo) }
            onPhotoLongClicked?.let { longClick ->
                root.setOnLongClickListener {
                    longClick(photo)
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int = photos.size

    fun updateData(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
