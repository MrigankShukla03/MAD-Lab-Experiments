package com.example.tripjournal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tripjournal.R
import com.example.tripjournal.activities.TripDetailsActivity
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.databinding.FragmentMapBinding
import com.example.tripjournal.models.Trip
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
    private lateinit var googleMap: GoogleMap
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.mapFragment, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        dbHelper = TripDatabaseHelper(requireContext())
        tripsList = dbHelper.getAllTrips()
        if (::googleMap.isInitialized) {
            setupMarkers()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMarkers()
    }

    private fun setupMarkers() {
        if (!::googleMap.isInitialized) return
        googleMap.clear()

        if (tripsList.isEmpty()) return

        val boundsBuilder = LatLngBounds.Builder()

        for (trip in tripsList) {
            val latLng = LatLng(trip.latitude, trip.longitude)
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(trip.title)
                    .snippet("${trip.location} | ${trip.date}")
            )
            marker?.tag = trip.id
            boundsBuilder.include(latLng)
        }

        googleMap.setOnInfoWindowClickListener { marker ->
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
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        } catch (e: Exception) {
            if (tripsList.isNotEmpty()) {
                val first = tripsList[0]
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(first.latitude, first.longitude), 12f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
