package com.example.vinilosapp.ui.musicians

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Performer

class MusicianListAdapter(
    private val onMusicianClick: (Performer) -> Unit
) : ListAdapter<Performer, MusicianListAdapter.MusicianViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicianViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_musician, parent, false)
        return MusicianViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicianViewHolder, position: Int) {
        val musician = getItem(position)
        holder.bind(musician)
        holder.itemView.setOnClickListener { onMusicianClick(musician) }
    }

    inner class MusicianViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {

        private val imgMusician: com.google.android.material.imageview.ShapeableImageView =
            itemView.findViewById(R.id.imgMusician)
        private val tvName: TextView = itemView.findViewById(R.id.tvMusicianName)
        private val tvAlbums: TextView = itemView.findViewById(R.id.tvMusicianAlbums)

        fun bind(musician: Performer) {
            tvName.text = musician.name
            tvAlbums.text = itemView.context.getString(R.string.musician_albums_placeholder)

            Glide.with(itemView)
                .load(musician.image)
                .placeholder(R.drawable.cover_1)
                .centerCrop()
                .into(imgMusician)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Performer>() {
        override fun areItemsTheSame(oldItem: Performer, newItem: Performer) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Performer, newItem: Performer) =
            oldItem == newItem
    }
}