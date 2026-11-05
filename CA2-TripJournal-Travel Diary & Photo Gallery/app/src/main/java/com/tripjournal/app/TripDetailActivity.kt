package com.tripjournal.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.tripjournal.app.adapters.PhotoGalleryAdapter
import com.tripjournal.app.databinding.ActivityTripDetailBinding
import com.tripjournal.app.db.DatabaseHelper
import com.tripjournal.app.models.Photo
import com.tripjournal.app.models.Trip

class TripDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTripDetailBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var photoAdapter: PhotoGalleryAdapter

    private var tripId: Long = -1
    private var currentTrip: Trip? = null

    private val pickGalleryPhotoLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val photo = Photo(tripId = tripId, photoUri = it.toString())
            dbHelper.insertPhoto(photo)
            Toast.makeText(this, "Photo added to gallery", Toast.LENGTH_SHORT).show()
            loadPhotos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTripDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarTripDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        tripId = intent.getLongExtra("TRIP_ID", -1)

        if (tripId == -1L) {
            Toast.makeText(this, "Trip not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupGalleryRecyclerView()

        binding.btnAddPhoto.setOnClickListener {
            pickGalleryPhotoLauncher.launch("image/*")
        }
    }

    override fun onResume() {
        super.onResume()
        loadTripDetails()
        loadPhotos()
    }

    private fun loadTripDetails() {
        val trips = dbHelper.getAllTrips()
        currentTrip = trips.find { it.id == tripId }
        currentTrip?.let { trip ->
            supportActionBar?.title = trip.title
            binding.tvDetailTitle.text = trip.title
            binding.tvDetailPlace.text = "📍 ${trip.place}"
            binding.tvDetailDate.text = trip.date
            binding.tvDetailCoords.text = "Coordinates: ${String.format("%.4f", trip.latitude)}, ${String.format("%.4f", trip.longitude)}"
            binding.tvDetailNotes.text = trip.notes.ifEmpty { "No notes provided for this trip." }

            if (!trip.coverPhotoUri.isNullOrEmpty()) {
                try {
                    binding.ivDetailCover.setImageURI(Uri.parse(trip.coverPhotoUri))
                } catch (e: Exception) {
                    binding.ivDetailCover.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                binding.ivDetailCover.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun setupGalleryRecyclerView() {
        photoAdapter = PhotoGalleryAdapter(
            photos = emptyList(),
            onPhotoClicked = { photo ->
                val intent = Intent(this, FullScreenPhotoActivity::class.java).apply {
                    putExtra("PHOTO_URI", photo.photoUri)
                }
                startActivity(intent)
            },
            onPhotoLongClicked = { photo ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Photo")
                    .setMessage("Do you want to remove this photo from the gallery?")
                    .setPositiveButton("Delete") { _, _ ->
                        dbHelper.deletePhoto(photo.id)
                        Toast.makeText(this, "Photo deleted", Toast.LENGTH_SHORT).show()
                        loadPhotos()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerViewPhotos.apply {
            layoutManager = GridLayoutManager(this@TripDetailActivity, 3)
            adapter = photoAdapter
        }
    }

    private fun loadPhotos() {
        val photos = dbHelper.getPhotosForTrip(tripId)
        photoAdapter.updateData(photos)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
