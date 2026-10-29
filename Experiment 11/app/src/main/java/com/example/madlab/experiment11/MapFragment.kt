package com.example.madlab.experiment11

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.madlab.experiment11.databinding.FragmentMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap

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

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragmentContainer) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.mapFragmentContainer, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Configure map UI settings
        try {
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isCompassEnabled = true
            googleMap.uiSettings.isMapToolbarEnabled = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Add University / Campus Landmarks
        val universityCampus = LatLng(12.9716, 77.5946) // Example coordinates (Bangalore Center / Tech Campus)
        val centralLibrary = LatLng(12.9725, 77.5955)
        val itLab = LatLng(12.9705, 77.5935)
        val studentCenter = LatLng(12.9710, 77.5960)

        googleMap.addMarker(MarkerOptions().position(universityCampus).title("University Main Campus"))
        googleMap.addMarker(MarkerOptions().position(centralLibrary).title("Central Library & Journals"))
        googleMap.addMarker(MarkerOptions().position(itLab).title("IT Technical Lab"))
        googleMap.addMarker(MarkerOptions().position(studentCenter).title("Student Activity Center"))

        // Move camera to campus with zoom level 15
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(universityCampus, 15f))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
