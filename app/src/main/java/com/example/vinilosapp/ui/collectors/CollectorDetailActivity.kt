package com.example.vinilosapp.ui.collectors

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.vinilosapp.R
import com.example.vinilosapp.databinding.ActivityCollectorDetailBinding
import com.example.vinilosapp.presentation.uistate.CollectorDetailUiState
import com.example.vinilosapp.presentation.viewmodel.CollectorDetailViewModel
import com.example.vinilosapp.ui.base.BaseActivity

class CollectorDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityCollectorDetailBinding
    private val viewModel: CollectorDetailViewModel by viewModels()
    private lateinit var albumAdapter: CollectorAlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCollectorDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val collectorId = intent.getIntExtra("collectorId", -1)
        if (collectorId == -1) {
            Toast.makeText(this, "Coleccionista no encontrado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Toolbar
        val btnNavIcon = binding.toolbar.root.findViewById<ImageView>(R.id.btnNavIcon)
        val refreshBtn = binding.toolbar.root.findViewById<ImageView>(R.id.refreshButton)
        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)
        btnNavIcon.setOnClickListener { finish() }
        refreshBtn.visibility = View.GONE

        // RecyclerView de álbumes
        albumAdapter = CollectorAlbumAdapter()
        binding.rvAlbums.apply {
            layoutManager = LinearLayoutManager(this@CollectorDetailActivity)
            adapter = albumAdapter
            isNestedScrollingEnabled = false
        }
        viewModel.loadCollector(collectorId)
        observeViewModel()
        setupBottomNav(2)
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                is CollectorDetailUiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.scrollView.visibility = View.GONE
                }
                is CollectorDetailUiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE

                    val collector = state.collector
                    binding.tvName.text = collector.name
                    binding.tvEmail.text = collector.email ?: ""
                    binding.tvPhone.text = collector.telephone ?: ""

                    Glide.with(this)
                        .load("")
                        .placeholder(R.drawable.cover_1)
                        .into(binding.imgCollector)

                    val albums = collector.collectorAlbums ?: emptyList()
                    if (albums.isEmpty()) {
                        binding.rvAlbums.visibility = View.GONE
                    } else {
                        binding.rvAlbums.visibility = View.VISIBLE
                        albumAdapter.submitList(albums)
                    }
                }
                is CollectorDetailUiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.scrollView.visibility = View.VISIBLE
                    Toast.makeText(this, state.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}