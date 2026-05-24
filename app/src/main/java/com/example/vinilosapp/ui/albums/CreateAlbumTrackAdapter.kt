package com.example.vinilosapp.ui.albums

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Track

class CreateAlbumTrackAdapter :
    ListAdapter<Track, CreateAlbumTrackAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track_created, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNumber: TextView = itemView.findViewById(R.id.tvTrackNumber)
        private val tvName: TextView = itemView.findViewById(R.id.tvTrackName)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvTrackDuration)

        fun bind(track: Track, position: Int) {
            tvNumber.text = String.format("%02d", position + 1)
            tvName.text = track.name
            tvDuration.text = track.duration ?: ""
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track) = oldItem == newItem
    }
}