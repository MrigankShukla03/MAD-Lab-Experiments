package com.example.tripjournal.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tripjournal.database.TripDatabaseHelper
import com.example.tripjournal.databinding.ItemTripBinding
import com.example.tripjournal.models.Trip

class TripAdapter(
    private var trips: List<Trip>,
    private val context: android.content.Context,
    private val onTripClicked: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    private val dbHelper = TripDatabaseHelper(context)

    inner class TripViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        with(holder.binding) {
            tvTitle.text = trip.title
            tvLocation.text = "📍 ${trip.location}"
            tvDate.text = trip.date

            val photoCount = dbHelper.getPhotosByTripId(trip.id).size
            tvPhotoCount.text = "📸 $photoCount Photos"

            if (!trip.coverPhotoUri.isNullOrEmpty()) {
                try {
                    ivCover.setImageURI(Uri.parse(trip.coverPhotoUri))
                } catch (e: Exception) {
                    ivCover.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                ivCover.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            root.setOnClickListener { onTripClicked(trip) }
        }
    }

    override fun getItemCount(): Int = trips.size

    fun updateData(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged()
    }
}
