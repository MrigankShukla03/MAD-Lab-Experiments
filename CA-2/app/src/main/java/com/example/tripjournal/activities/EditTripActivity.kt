package com.example.tripjournal.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.tripjournal.databinding.ActivityAddTripBinding
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.models.Trip
import java.util.Calendar

class EditTripActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTripBinding
    private lateinit var dbHelper: TripDatabaseHelper

    private var tripId: Long = -1
    private var currentTrip: Trip? = null
    private var selectedLat: Double = 12.9716
    private var selectedLng: Double = 77.5946
    private var locationName: String = ""
    private var coverPhotoUri: String? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddTrip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Trip"

        dbHelper = TripDatabaseHelper(this)
        tripId = intent.getLongExtra("TRIP_ID", -1)

        currentTrip = dbHelper.getTripById(tripId)
        currentTrip?.let { trip ->
            binding.etTitle.setText(trip.title)
            binding.etDate.setText(trip.date)
            binding.etNotes.setText(trip.notes)
            locationName = trip.location
            selectedLat = trip.latitude
            selectedLng = trip.longitude
            coverPhotoUri = trip.coverPhotoUri

            binding.tvSelectedLocation.text = "Location: $locationName"

            if (!coverPhotoUri.isNullOrEmpty()) {
                try {
                    binding.ivCoverPreview.setImageURI(Uri.parse(coverPhotoUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

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

        binding.btnAddPhotos.visibility = View.GONE
        binding.recyclerViewSelectedPhotos.visibility = View.GONE

        binding.btnSaveTrip.text = "Update Trip"
        binding.btnSaveTrip.setOnClickListener {
            updateTrip()
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

    private fun updateTrip() {
        val title = binding.etTitle.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val notes = binding.etNotes.text.toString().trim()

        if (title.isEmpty()) {
            binding.etTitle.error = "Trip Title is required"
            return
        }

        val updated = Trip(
            id = tripId,
            title = title,
            location = locationName,
            latitude = selectedLat,
            longitude = selectedLng,
            date = date,
            notes = notes,
            coverPhotoUri = coverPhotoUri
        )

        dbHelper.updateTrip(updated)
        Toast.makeText(this, "Trip updated successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
