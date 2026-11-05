package com.example.tripjournal.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripjournal.adapters.PhotoAdapter
import com.example.tripjournal.databinding.ActivityAddTripBinding
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.models.Photo
import com.example.tripjournal.models.Trip
import java.util.Calendar

class AddTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTripBinding
    private lateinit var dbHelper: TripDatabaseHelper
    private lateinit var photoAdapter: PhotoAdapter

    private var selectedLat: Double = 12.9716
    private var selectedLng: Double = 77.5946
    private var locationName: String = ""
    private var coverPhotoUri: String? = null
    private val selectedPhotosUris = mutableListOf<String>()

    private val mapPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                selectedLat = data.getDoubleExtra("LAT", 12.9716)
                selectedLng = data.getDoubleExtra("LNG", 77.5946)
                locationName = data.getStringExtra("LOCATION_NAME") ?: "Selected Location"
                binding.tvSelectedLocation.text = "Location: $locationName"
            }
        }
    }

    private val coverPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            coverPhotoUri = it.toString()
            binding.ivCoverPreview.setImageURI(it)
        }
    }

    private val multiPhotosLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        for (uri in uris) {
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedPhotosUris.add(uri.toString())
        }
        val photoList = selectedPhotosUris.map { Photo(tripId = 0, photoUri = it) }
        photoAdapter.updateData(photoList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddTrip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = TripDatabaseHelper(this)

        setupPhotosRecyclerView()

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnChooseLocation.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java).apply {
                putExtra("LAT", selectedLat)
                putExtra("LNG", selectedLng)
            }
            mapPickerLauncher.launch(intent)
        }

        binding.btnSelectCover.setOnClickListener {
            coverPhotoLauncher.launch("image/*")
        }

        binding.btnAddPhotos.setOnClickListener {
            multiPhotosLauncher.launch("image/*")
        }

        binding.btnSaveTrip.setOnClickListener {
            saveTrip()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val dateStr = String.format("%02d-%02d-%d", day, month + 1, year)
                binding.etDate.setText(dateStr)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupPhotosRecyclerView() {
        photoAdapter = PhotoAdapter(
            photos = emptyList(),
            onPhotoClicked = {},
            onPhotoLongClicked = { photo ->
                selectedPhotosUris.remove(photo.photoUri)
                photoAdapter.updateData(selectedPhotosUris.map { Photo(0, 0, it) })
                Toast.makeText(this, "Photo removed", Toast.LENGTH_SHORT).show()
            }
        )
        binding.recyclerViewSelectedPhotos.apply {
            layoutManager = LinearLayoutManager(this@AddTripActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = photoAdapter
        }
    }

    private fun saveTrip() {
        val title = binding.etTitle.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Trip Title is required"
            return
        }
        if (locationName.isEmpty()) {
            Toast.makeText(this, "Please choose a location from the map", Toast.LENGTH_SHORT).show()
            return
        }
        if (date.isEmpty()) {
            binding.etDate.error = "Travel Date is required"
            return
        }

        val trip = Trip(
            title = title,
            location = locationName,
            latitude = selectedLat,
            longitude = selectedLng,
            date = date,
            notes = notes,
            coverPhotoUri = coverPhotoUri
        )

        val tripId = dbHelper.insertTrip(trip)
        if (tripId != -1L) {
            for (uriStr in selectedPhotosUris) {
                dbHelper.insertPhoto(Photo(tripId = tripId, photoUri = uriStr))
            }
            Toast.makeText(this, "Trip saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Failed to save trip", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
