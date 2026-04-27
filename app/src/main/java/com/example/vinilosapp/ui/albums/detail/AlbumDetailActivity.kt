package com.example.vinilosapp.ui.albums.detail

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vinilosapp.R
import com.example.vinilosapp.databinding.ActivityAlbumDetailBinding
import com.example.vinilosapp.presentation.uistate.AlbumDetailUiState
import com.example.vinilosapp.presentation.viewmodel.AlbumDetailViewModel

class AlbumDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumDetailBinding
    private val viewModel: AlbumDetailViewModel by viewModels()
    private lateinit var trackAdapter: TrackAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val albumId = intent.getIntExtra("albumId", -1)
        if (albumId == -1) {
            Toast.makeText(this, "Álbum no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnNavIcon = binding.toolbar.root.findViewById<ImageView>(R.id.btnNavIcon)
        val refreshBtn = binding.toolbar.root.findViewById<ImageView>(R.id.refreshButton)
        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)
        btnNavIcon.setOnClickListener { finish() }
        refreshBtn.visibility = View.GONE

        setupRecyclerView()
        viewModel.loadAlbum(albumId)
        observeViewModel()
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter(emptyList())
        binding.rvTracks.apply {
            layoutManager = LinearLayoutManager(this@AlbumDetailActivity)
            adapter = trackAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is AlbumDetailUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }
                is AlbumDetailUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE

                    val album = state.album
                    binding.tvName.text = album.name
                    binding.tvDescription.text = formatAlbumDetailDescription(album.description, "Sin descripción")
                    binding.tvRecordLabel.text = formatAlbumDetailRecordLabel(album.recordLabel)
                    binding.tvArtist.text = formatAlbumDetailArtistName(album.performers, "Artista desconocido")
                    binding.tvYear.text = formatAlbumDetailYear(album.releaseDate)

                    Glide.with(this)
                        .load(album.cover)
                        .placeholder(R.drawable.cover_1)
                        .into(binding.imgCover)

                    val tracks = album.tracks ?: emptyList()
                    if (tracks.isEmpty()) {
                        binding.rvTracks.visibility = View.GONE
                    } else {
                        binding.rvTracks.visibility = View.VISIBLE
                        trackAdapter.updateTracks(tracks)
                    }
                }
                is AlbumDetailUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}