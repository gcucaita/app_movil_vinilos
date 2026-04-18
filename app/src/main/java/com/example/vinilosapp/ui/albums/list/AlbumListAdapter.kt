package com.example.vinilosapp.ui.albums.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Album

class AlbumListAdapter : ListAdapter<Album, AlbumListAdapter.AlbumViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_album, parent, false)
        return AlbumViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coverImage: ImageView = itemView.findViewById(R.id.albumCoverImage)
        private val titleText: TextView = itemView.findViewById(R.id.albumTitleText)
        private val artistText: TextView = itemView.findViewById(R.id.albumArtistText)
        private val metaText: TextView = itemView.findViewById(R.id.albumMetaText)

        fun bind(album: Album) {
            titleText.text = album.name
            artistText.text = album.performers?.firstOrNull()?.name ?: itemView.context.getString(R.string.unknown_artist)
            metaText.text = buildMeta(album)

            Glide.with(itemView)
                .load(album.cover)
                .placeholder(R.drawable.cover_1)
                .error(R.drawable.cover_2)
                .centerCrop()
                .into(coverImage)
        }

        private fun buildMeta(album: Album): String {
            val year = album.releaseDate?.take(4)?.takeIf { it.all(Char::isDigit) } ?: "----"
            val label = album.recordLabel?.uppercase()?.ifBlank { "N/A" } ?: "N/A"
            return "$year · $label"
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean = oldItem == newItem
    }
}
