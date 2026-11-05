package com.tripjournal.app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tripjournal.app.databinding.ItemPhotoBinding
import com.tripjournal.app.models.Photo

class PhotoGalleryAdapter(
    private var photos: List<Photo>,
    private val onPhotoClicked: (Photo) -> Unit,
    private val onPhotoLongClicked: (Photo) -> Unit
) : RecyclerView.Adapter<PhotoGalleryAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
        with(holder.binding) {
            try {
                ivGalleryThumb.setImageURI(Uri.parse(photo.photoUri))
            } catch (e: Exception) {
                ivGalleryThumb.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            root.setOnClickListener { onPhotoClicked(photo) }
            root.setOnLongClickListener {
                onPhotoLongClicked(photo)
                true
            }
        }
    }

    override fun getItemCount(): Int = photos.size

    fun updateData(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
