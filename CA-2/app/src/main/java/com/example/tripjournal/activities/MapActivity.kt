package com.example.tripjournal.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tripjournal.R
import com.example.tripjournal.databinding.ActivityMapOverviewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapOverviewBinding
    private lateinit var googleMap: GoogleMap
    private var selectedLatLng: LatLng = LatLng(12.9716, 77.5946)
    private var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMapOverview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Choose Visit Location"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapOverviewContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().replace(R.id.mapOverviewContainer, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        val initialLat = intent.getDoubleExtra("LAT", 12.9716)
        val initialLng = intent.getDoubleExtra("LNG", 77.5946)
        selectedLatLng = LatLng(initialLat, initialLng)

        updateMarker(selectedLatLng)

        googleMap.setOnMapLongClickListener { latLng ->
            selectedLatLng = latLng
            updateMarker(selectedLatLng)
            Toast.makeText(this, "Location selected! Tap back to confirm.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMarker(latLng: LatLng) {
        currentMarker?.remove()
        currentMarker = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Selected Visit Location")
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
    }

    override fun onBackPressed() {
        val intent = Intent().apply {
            putExtra("LAT", selectedLatLng.latitude)
            putExtra("LNG", selectedLatLng.longitude)
            putExtra("LOCATION_NAME", "Location (${String.format("%.2f", selectedLatLng.latitude)}, ${String.format("%.2f", selectedLatLng.longitude)})")
        }
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
