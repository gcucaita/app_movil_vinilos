package com.example.vinilosapp.ui.albums.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.databinding.ItemTrackBinding
import com.example.vinilosapp.domain.model.Track

class TrackAdapter(private var tracks: List<Track>) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    inner class TrackViewHolder(private val binding: ItemTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track, position: Int) {
            binding.tvTrackNumber.text = formatTrackNumber(position)
            binding.tvTrackName.text = track.name
            binding.tvTrackDuration.text = formatTrackDuration(track.duration)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position], position)
    }

    override fun getItemCount() = tracks.size

    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}