package com.example.vinilosapp.ui.collectors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Collector
import com.google.android.material.imageview.ShapeableImageView

class CollectorListAdapter(
    private val onCollectorClick: (Collector) -> Unit
) : ListAdapter<Collector, CollectorListAdapter.CollectorViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collector, parent, false)
        return CollectorViewHolder(view)
    }

    override fun onBindViewHolder(holder: CollectorViewHolder, position: Int) {
        val collector = getItem(position)
        holder.bind(collector)
        holder.itemView.setOnClickListener { onCollectorClick(collector) }
    }

    inner class CollectorViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgCollector: ShapeableImageView = itemView.findViewById(R.id.imgCollector)
        private val tvName: TextView = itemView.findViewById(R.id.tvCollectorName)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvCollectorEmail)

        fun bind(collector: Collector) {
            tvName.text = collector.name
            tvEmail.text = collector.email ?: ""
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Collector>() {
        override fun areItemsTheSame(oldItem: Collector, newItem: Collector) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Collector, newItem: Collector) =
            oldItem == newItem
    }
}