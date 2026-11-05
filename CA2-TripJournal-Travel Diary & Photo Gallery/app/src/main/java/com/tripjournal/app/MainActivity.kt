package com.tripjournal.app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tripjournal.app.adapters.TripAdapter
import com.tripjournal.app.databinding.ActivityMainBinding
import com.tripjournal.app.db.DatabaseHelper
import com.tripjournal.app.models.Trip

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tripAdapter: TripAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        dbHelper = DatabaseHelper(this)

        setupRecyclerView()

        binding.fabAddTrip.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java)
            startActivity(intent)
        }

        binding.btnViewMapOverview.setOnClickListener {
            val intent = Intent(this, MapOverviewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadTrips()
    }

    private fun setupRecyclerView() {
        tripAdapter = TripAdapter(
            trips = emptyList(),
            onTripClicked = { trip ->
                val intent = Intent(this, TripDetailActivity::class.java).apply {
                    putExtra("TRIP_ID", trip.id)
                }
                startActivity(intent)
            },
            onEditClicked = { trip ->
                val intent = Intent(this, AddTripActivity::class.java).apply {
                    putExtra("TRIP_ID", trip.id)
                    putExtra("IS_EDIT", true)
                    putExtra("TITLE", trip.title)
                    putExtra("PLACE", trip.place)
                    putExtra("NOTES", trip.notes)
                    putExtra("DATE", trip.date)
                    putExtra("LAT", trip.latitude)
                    putExtra("LNG", trip.longitude)
                    putExtra("COVER", trip.coverPhotoUri)
                }
                startActivity(intent)
            },
            onDeleteClicked = { trip ->
                AlertDialog.Builder(this)
                    .setTitle("Delete Trip")
                    .setMessage("Are you sure you want to delete '${trip.title}' and all its photos?")
                    .setPositiveButton("Delete") { _, _ ->
                        dbHelper.deleteTrip(trip.id)
                        Toast.makeText(this, "Trip deleted", Toast.LENGTH_SHORT).show()
                        loadTrips()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        )

        binding.recyclerViewTrips.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tripAdapter
        }
    }

    private fun loadTrips() {
        val trips = dbHelper.getAllTrips()
        tripAdapter.updateData(trips)
    }
}
