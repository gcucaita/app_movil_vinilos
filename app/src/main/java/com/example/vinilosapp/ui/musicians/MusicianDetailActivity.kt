package com.example.vinilosapp.ui.musicians

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.vinilosapp.R
import com.example.vinilosapp.databinding.ActivityMusicianDetailBinding
import com.example.vinilosapp.presentation.uistate.MusicianDetailUiState
import com.example.vinilosapp.presentation.viewmodel.MusicianDetailViewModel
import com.example.vinilosapp.ui.base.BaseActivity

class MusicianDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityMusicianDetailBinding
    private val viewModel: MusicianDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMusicianDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicianId = intent.getIntExtra("musicianId", -1)

        if (musicianId == -1) {
            Toast.makeText(this, "Artista no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()

        // Tab de artistas/músicos
        setupBottomNav(1)

        viewModel.loadMusician(musicianId)

        observeViewModel()
    }

    private fun setupToolbar() {

        val btnNavIcon =
            binding.toolbar.root.findViewById<ImageView>(R.id.btnNavIcon)

        val refreshBtn =
            binding.toolbar.root.findViewById<ImageView>(R.id.refreshButton)

        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)

        btnNavIcon.setOnClickListener {
            finish()
        }

        refreshBtn.visibility = View.GONE
    }

    private fun observeViewModel() {

        viewModel.uiState.observe(this) { state ->

            when (state) {

                is MusicianDetailUiState.Loading -> {

                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }

                is MusicianDetailUiState.Success -> {

                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE

                    val musician = state.musician

                    binding.tvName.text = musician.name

                    binding.tvDescription.text =
                        musician.description ?: "Sin descripción"

                    binding.tvDate.text =
                        musician.birthDate
                            ?: musician.creationDate
                                    ?: ""

                    Glide.with(this)
                        .load(musician.image)
                        .placeholder(R.drawable.cover_1)
                        .into(binding.imgMusician)
                }

                is MusicianDetailUiState.Error -> {

                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE

                    Toast.makeText(
                        this,
                        state.message ?: "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}