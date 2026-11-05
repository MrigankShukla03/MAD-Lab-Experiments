package com.example.tripjournal.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tripjournal.adapters.PhotoAdapter
import com.example.tripjournal.databinding.ActivityTripDetailsBinding
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.models.Photo
import com.example.tripjournal.models.Trip

class TripDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripDetailsBinding
    private lateinit var dbHelper: TripDatabaseHelper
    private lateinit var photoAdapter: PhotoAdapter

    private var tripId: Long = -1
    private var currentTrip: Trip? = null

    private val addMorePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dbHelper.insertPhoto(Photo(tripId = tripId, photoUri = it.toString()))
            Toast.makeText(this, "Photo added to gallery", Toast.LENGTH_SHORT).show()
            loadPhotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTripDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = TripDatabaseHelper(this)
        tripId = intent.getLongExtra("TRIP_ID", -1)

        if (tripId == -1L) {
            Toast.makeText(this, "Trip not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupGalleryRecyclerView()

        binding.btnAddPhotoDetail.setOnClickListener {
            addMorePhotoLauncher.launch("image/*")
        }

        binding.btnEditTrip.setOnClickListener {
            val intent = Intent(this, EditTripActivity::class.java).apply {
                putExtra("TRIP_ID", tripId)
            }
            startActivity(intent)
        }

        binding.btnDeleteTrip.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Trip")
                .setMessage("Are you sure you want to delete this trip and all associated photos?")
                .setPositiveButton("Delete") { _, _ ->
                    dbHelper.deletePhotosByTripId(tripId)
                    dbHelper.deleteTrip(tripId)
                    Toast.makeText(this, "Trip deleted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTripDetails()
        loadPhotos()
    }

    private fun loadTripDetails() {
        currentTrip = dbHelper.getTripById(tripId)
        currentTrip?.let { trip ->
            supportActionBar?.title = trip.title
            binding.tvTitleDetail.text = trip.title
            binding.tvLocationDetail.text = "📍 ${trip.location}"
            binding.tvDateDetail.text = trip.date
            binding.tvCoordsDetail.text = "Coordinates: ${String.format("%.4f", trip.latitude)}, ${String.format("%.4f", trip.longitude)}"
            binding.tvNotesDetail.text = trip.notes.ifEmpty { "No notes provided for this journey." }

            if (!trip.coverPhotoUri.isNullOrEmpty()) {
                try {
                    binding.ivCoverPhotoDetail.setImageURI(Uri.parse(trip.coverPhotoUri))
                } catch (e: Exception) {
                    binding.ivCoverPhotoDetail.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.ivCoverPhotoDetail.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun setupGalleryRecyclerView() {
        photoAdapter = PhotoAdapter(
            photos = emptyList(),
            onPhotoClicked = { photo ->
                val intent = Intent(this, ImageViewerActivity::class.java).apply {
                    putExtra("PHOTO_URI", photo.photoUri)
                }
                startActivity(intent)
            },
            onPhotoLongClicked = { photo ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Photo")
                    .setMessage("Remove this photo from the gallery?")
                    .setPositiveButton("Delete") { _, _ ->
                        dbHelper.deletePhoto(photo.id)
                        Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show()
                        loadPhotos()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerViewDetailPhotos.apply {
            layoutManager = GridLayoutManager(this@TripDetailsActivity, 3)
            adapter = photoAdapter
        }
    }

    private fun loadPhotos() {
        val photos = dbHelper.getPhotosByTripId(tripId)
        photoAdapter.updateData(photos)
        binding.tvPhotoCount.text = "Photo Gallery (${photos.size})"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
