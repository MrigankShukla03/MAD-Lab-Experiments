package com.tripjournal.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tripjournal.app.databinding.ActivityMapOverviewBinding
import com.tripjournal.app.db.DatabaseHelper
import com.tripjournal.app.models.Trip
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapOverviewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapOverviewBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var googleMap: GoogleMap
    private var tripsList: List<Trip> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMapOverview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DatabaseHelper(this)
        tripsList = dbHelper.getAllTrips()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapOverviewContainer) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (tripsList.isEmpty()) {
            Toast.makeText(this, "No trips recorded yet. Add a trip to view on map.", Toast.LENGTH_LONG).show()
            return
        }

        val boundsBuilder = LatLngBounds.Builder()
        for (trip in tripsList) {
            val latLng = LatLng(trip.latitude, trip.longitude)
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(trip.title)
                    .snippet("${trip.place} - ${trip.date}")
            )
            boundsBuilder.include(latLng)
        }

        try {
            val bounds = boundsBuilder.build()
            val padding = 120 // offset from edges in pixels
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            googleMap.animateCamera(cameraUpdate)
        } catch (e: Exception) {
            // Fallback if single point
            if (tripsList.isNotEmpty()) {
                val first = tripsList[0]
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 12f))
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
