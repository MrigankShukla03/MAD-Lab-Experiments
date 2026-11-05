package com.example.tripjournal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tripjournal.activities.ImageViewerActivity
import com.example.tripjournal.adapters.PhotoAdapter
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.databinding.FragmentMemoriesBinding
import com.example.tripjournal.models.Photo

class MemoriesFragment : Fragment() {

    private var _binding: FragmentMemoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: TripDatabaseHelper
    private lateinit var photoAdapter: PhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TripDatabaseHelper(requireContext())

        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadPhotos()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter(
            photos = emptyList(),
            onPhotoClicked = { photo ->
                val intent = Intent(requireContext(), ImageViewerActivity::class.java).apply {
                    putExtra("PHOTO_URI", photo.photoUri)
                }
                startActivity(intent)
            }
        )

        binding.recyclerViewMemories.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = photoAdapter
        }
    }

    private fun loadPhotos() {
        val allPhotos = dbHelper.getAllPhotos()
        photoAdapter.updateData(allPhotos)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
