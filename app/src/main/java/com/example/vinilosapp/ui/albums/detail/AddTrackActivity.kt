package com.example.vinilosapp.ui.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.vinilosapp.R
import com.example.vinilosapp.databinding.ActivityAddTrackBinding
import com.example.vinilosapp.presentation.uistate.AddTrackUiState
import com.example.vinilosapp.presentation.viewmodel.CreateAlbumViewModel
import com.example.vinilosapp.ui.base.BaseActivity

class AddTrackActivity : BaseActivity() {

    private lateinit var binding: ActivityAddTrackBinding
    private val viewModel: CreateAlbumViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTrackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val albumId = intent.getIntExtra("albumId", -1)
        if (albumId == -1) {
            Toast.makeText(this, "Álbum no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel.createdAlbumId = albumId

        val btnNavIcon = binding.toolbar.root.findViewById<ImageView>(R.id.btnNavIcon)
        val refreshBtn = binding.toolbar.root.findViewById<ImageView>(R.id.refreshButton)
        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)
        btnNavIcon.setOnClickListener { finish() }
        refreshBtn.visibility = View.GONE

        binding.btnAddTrack.setOnClickListener {
            val name = binding.etTrackName.text.toString().trim()
            val duration = binding.etTrackDuration.text.toString().trim()
            if (name.isEmpty() || duration.isEmpty()) {
                Toast.makeText(this, "Completa nombre y duración", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addTrack(name, duration)
        }

        observeViewModel()
        setupBottomNav(0)
    }

    private fun observeViewModel() {
        viewModel.trackState.observe(this) { state ->
            when (state) {
                is AddTrackUiState.Loading -> binding.btnAddTrack.isEnabled = false
                is AddTrackUiState.Success -> {
                    binding.btnAddTrack.isEnabled = true
                    binding.etTrackName.text?.clear()
                    binding.etTrackDuration.text?.clear()
                    Toast.makeText(this, "Track agregado", Toast.LENGTH_SHORT).show()
                }
                is AddTrackUiState.Error -> {
                    binding.btnAddTrack.isEnabled = true
                    Toast.makeText(this, state.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
                is AddTrackUiState.Idle -> {}
            }
        }

        viewModel.tracks.observe(this) { tracks ->
            binding.tvTrackCount.text = "${tracks.size} TRACKS ADDED"
            binding.llTrackList.removeAllViews()
            tracks.forEachIndexed { index, track ->
                val row = LayoutInflater.from(this)
                    .inflate(R.layout.item_track_row, binding.llTrackList, false)
                row.findViewById<TextView>(R.id.tvTrackIndex).text = "%02d".format(index + 1)
                row.findViewById<TextView>(R.id.tvTrackName).text = track.name
                row.findViewById<TextView>(R.id.tvTrackDuration).text = track.duration
                binding.llTrackList.addView(row)
            }
        }
    }
}