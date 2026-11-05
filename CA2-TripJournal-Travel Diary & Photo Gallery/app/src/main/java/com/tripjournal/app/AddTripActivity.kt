package com.tripjournal.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tripjournal.app.databinding.ActivityAddTripBinding
import com.tripjournal.app.db.DatabaseHelper
import com.tripjournal.app.models.Trip
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTripActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityAddTripBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var googleMap: GoogleMap

    private var selectedLatLng: LatLng = LatLng(12.9716, 77.5946) // Default coordinate
    private var selectedCoverUri: String? = null
    private var isEditMode = false
    private var tripId: Long = -1

    private val pickCoverLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Take persistable URI permission if needed or store string representation
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedCoverUri = it.toString()
            binding.ivCoverPreview.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddTrip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)

        isEditMode = intent.getBooleanExtra("IS_EDIT", false)
        if (isEditMode) {
            tripId = intent.getLongExtra("TRIP_ID", -1)
            binding.etTripTitle.setText(intent.getStringExtra("TITLE"))
            binding.etPlaceName.setText(intent.getStringExtra("PLACE"))
            binding.etTripNotes.setText(intent.getStringExtra("NOTES"))
            binding.etTripDate.setText(intent.getStringExtra("DATE"))
            selectedCoverUri = intent.getStringExtra("COVER")
            val lat = intent.getDoubleExtra("LAT", 12.9716)
            val lng = intent.getDoubleExtra("LNG", 77.5946)
            selectedLatLng = LatLng(lat, lng)

            if (!selectedCoverUri.isNullOrEmpty()) {
                try {
                    binding.ivCoverPreview.setImageURI(Uri.parse(selectedCoverUri))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            supportActionBar?.title = "Edit Trip"
            binding.btnSaveTrip.text = "Update Trip"
        } else {
            val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
            binding.etTripDate.setText(currentDate)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapAddTripContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSelectCover.setOnClickListener {
            pickCoverLauncher.launch("image/*")
        }

        binding.btnSaveTrip.setOnClickListener {
            saveTrip()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        updateMarkerPosition()

        googleMap.setOnMapLongClickListener { latLng ->
            selectedLatLng = latLng
            updateMarkerPosition()
            Toast.makeText(this, "Location pinned!", Toast.LENGTH_SHORT).show()
        }
    }

    private var currentMarker: Marker? = null
    private fun updateMarkerPosition() {
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(
            MarkerOptions().position(selectedLatLng).title("Selected Visit Location")
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 14f))
        binding.tvSelectedCoords.text = "Selected Coordinates: ${String.format("%.4f", selectedLatLng.latitude)}, ${String.format("%.4f", selectedLatLng.longitude)}"
    }

    private fun saveTrip() {
        val title = binding.etTripTitle.text.toString().trim()
        val place = binding.etPlaceName.text.toString().trim()
        val notes = binding.etTripNotes.text.toString().trim()
        val date = binding.etTripDate.text.toString().trim()

        if (title.isEmpty() || place.isEmpty()) {
            Toast.makeText(this, "Please enter Trip Title and Place", Toast.LENGTH_SHORT).show()
            return
        }

        val trip = Trip(
            id = if (isEditMode) tripId else 0,
            title = title,
            place = place,
            notes = notes,
            date = date.ifEmpty { "15-10-2026" },
            latitude = selectedLatLng.latitude,
            longitude = selectedLatLng.longitude,
            coverPhotoUri = selectedCoverUri
        )

        if (isEditMode) {
            dbHelper.updateTrip(trip)
            Toast.makeText(this, "Trip updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertTrip(trip)
            Toast.makeText(this, "Trip recorded successfully", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
