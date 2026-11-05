package com.example.tripjournal.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
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
    private var isWebMapMode = false

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

        setupNativeMap()
        setupEmbeddedWebView()

        binding.btnToggleMapMode.setOnClickListener {
            isWebMapMode = !isWebMapMode
            if (isWebMapMode) {
                binding.containerNativeMap.visibility = View.GONE
                binding.containerWebMap.visibility = View.VISIBLE
                binding.btnToggleMapMode.text = "Switch to Native Google Map"
                Toast.makeText(context, "Showing Embedded Google Maps Iframe", Toast.LENGTH_SHORT).show()
            } else {
                binding.containerNativeMap.visibility = View.VISIBLE
                binding.containerWebMap.visibility = View.GONE
                binding.btnToggleMapMode.text = "Switch to Embedded Web Map (Iframe)"
                Toast.makeText(context, "Showing Native Google Map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupNativeMap() {
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
            }
        }
    }

    private fun setupEmbeddedWebView() {
        binding.webViewEmbedMap.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webViewClient = WebViewClient()

            val embedHtml = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body, html { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #F8F8F2; }
                        iframe { width: 100%; height: 100%; border: 0; }
                    </style>
                </head>
                <body>
                    <iframe
                      width="100%"
                      height="100%"
                      style="border:0;"
                      loading="lazy"
                      allowfullscreen
                      referrerpolicy="strict-origin-when-cross-origin"
                      src="https://www.google.com/maps/embed/v1/place?key=AIzaSyCnL0xWyoF4yCBfkguBirgsUXaOgcmjBaA&q=Space+Needle,Seattle+WA">
                    </iframe>
                </body>
                </html>
            """.trimIndent()

            loadDataWithBaseURL(null, embedHtml, "text/html", "UTF-8", null)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
