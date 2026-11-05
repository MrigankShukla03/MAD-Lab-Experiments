package com.tripjournal.app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tripjournal.app.databinding.ItemTripBinding
import com.tripjournal.app.models.Trip

class TripAdapter(
    private var trips: List<Trip>,
    private val onTripClicked: (Trip) -> Unit,
    private val onEditClicked: (Trip) -> Unit,
    private val onDeleteClicked: (Trip) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    inner class TripViewHolder(val binding: ItemTripBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        with(holder.binding) {
            tvTripTitle.text = trip.title
            tvTripPlace.text = "📍 ${trip.place}"
            tvTripDate.text = trip.date

            if (!trip.coverPhotoUri.isNullOrEmpty()) {
                try {
                    ivCoverPhoto.setImageURI(Uri.parse(trip.coverPhotoUri))
                } catch (e: Exception) {
                    ivCoverPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
                }
            } else {
                ivCoverPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            root.setOnClickListener { onTripClicked(trip) }
            btnEditTrip.setOnClickListener { onEditClicked(trip) }
            btnDeleteTrip.setOnClickListener { onDeleteClicked(trip) }
        }
    }

    override fun getItemCount(): Int = trips.size

    fun updateData(newTrips: List<Trip>) {
        trips = newTrips
        notifyDataSetChanged()
    }
}
