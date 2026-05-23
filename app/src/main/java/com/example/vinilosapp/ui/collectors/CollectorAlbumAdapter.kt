package com.example.vinilosapp.ui.collectors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.CollectorAlbum

class CollectorAlbumAdapter :
    ListAdapter<CollectorAlbum, CollectorAlbumAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collector_album, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvStatus: TextView = itemView.findViewById(R.id.tvAlbumStatus)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvAlbumPrice)

        fun bind(album: CollectorAlbum) {
            tvStatus.text = album.status ?: "N/A"
            tvPrice.text = album.price?.let { "$$it" } ?: ""
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CollectorAlbum>() {
        override fun areItemsTheSame(oldItem: CollectorAlbum, newItem: CollectorAlbum) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CollectorAlbum, newItem: CollectorAlbum) =
            oldItem == newItem
    }
}