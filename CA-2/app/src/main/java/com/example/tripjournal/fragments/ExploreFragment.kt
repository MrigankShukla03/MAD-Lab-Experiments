package com.example.tripjournal.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tripjournal.activities.AddTripActivity
import com.example.tripjournal.activities.TripDetailsActivity
import com.example.tripjournal.adapters.TripAdapter
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.databinding.FragmentExploreBinding
import com.example.tripjournal.models.Trip

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: TripDatabaseHelper
    private lateinit var tripAdapter: TripAdapter
    private var allTrips: List<Trip> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = TripDatabaseHelper(requireContext())

        setupRecyclerView()

        binding.fabAddTrip.setOnClickListener {
            val intent = Intent(requireContext(), AddTripActivity::class.java)
            startActivity(intent)
        }

        binding.btnFirstJourney.setOnClickListener {
            val intent = Intent(requireContext(), AddTripActivity::class.java)
            startActivity(intent)
        }

        binding.etSearchTrip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTrips(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        loadTrips()
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter(
            trips = emptyList(),
            context = requireContext(),
            onTripClicked = { trip ->
                val intent = Intent(requireContext(), TripDetailsActivity::class.java).apply {
                    putExtra("TRIP_ID", trip.id)
                }
                startActivity(intent)
            }
        )

        binding.recyclerViewExplore.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tripAdapter
        }
    }

    private fun loadTrips() {
        allTrips = dbHelper.getAllTrips()
        updateUI(allTrips)
    }

    private fun filterTrips(query: String) {
        val filtered = if (query.isEmpty()) {
            allTrips
        } else {
            allTrips.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.location.contains(query, ignoreCase = true) ||
                        it.notes.contains(query, ignoreCase = true)
            }
        }
        updateUI(filtered)
    }

    private fun updateUI(trips: List<Trip>) {
        tripAdapter.updateData(trips)
        if (trips.isEmpty()) {
            binding.recyclerViewExplore.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerViewExplore.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
