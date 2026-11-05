package com.example.tripjournal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tripjournal.R
import com.example.tripjournal.activities.TripDetailsActivity
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.databinding.FragmentMapBinding
import com.example.tripjournal.models.Trip
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: TripDatabaseHelper
    private var googleMap: GoogleMap? = null
    private var tripsList: List<Trip> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TripDatabaseHelper(requireContext())
        tripsList = dbHelper.getAllTrips()

        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(requireContext())

        if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
            try {
                val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
                    ?: SupportMapFragment.newInstance().also {
                        childFragmentManager.beginTransaction().replace(R.id.mapFragment, it).commit()
                    }
                mapFragment.getMapAsync(this)
            } catch (e: Exception) {
                e.printStackTrace()
                showDemoMapFallback()
            }
        } else {
            Toast.makeText(context, "Google Play Services unavailable. Running in Demo Map Mode.", Toast.LENGTH_LONG).show()
            showDemoMapFallback()
        }
    }

    override fun onResume() {
        super.onResume()
        dbHelper = TripDatabaseHelper(requireContext())
        tripsList = dbHelper.getAllTrips()
        if (googleMap != null) {
            setupMarkers()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMarkers()
    }

    private fun setupMarkers() {
        val map = googleMap ?: return
        map.clear()

        if (tripsList.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()

        for (trip in tripsList) {
            val latLng = LatLng(trip.latitude, trip.longitude)
            val marker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(trip.title)
                    .snippet("${trip.location} | ${trip.date}")
            )
            marker?.tag = trip.id
            boundsBuilder.include(latLng)
        }

        map.setOnInfoWindowClickListener { marker ->
            val tripId = marker.tag as? Long
            tripId?.let {
                val intent = Intent(requireContext(), TripDetailsActivity::class.java).apply {
                    putExtra("TRIP_ID", it)
                }
                startActivity(intent)
            }
        }

        try {
            val bounds = boundsBuilder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        } catch (e: Exception) {
            if (tripsList.isNotEmpty()) {
                val first = tripsList[0]
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 12f))
            }
        }
    }

    private fun showDemoMapFallback() {
        // Fallback UI or toast when map cannot render
        Toast.makeText(context, "Demo Map Mode Active: ${tripsList.size} locations loaded.", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
