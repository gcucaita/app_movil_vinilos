package com.example.vinilosapp.ui.albums

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.vinilosapp.MainActivity
import com.example.vinilosapp.R
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.databinding.ActivityCreateAlbumBinding
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.CreateAlbumUiState
import com.example.vinilosapp.presentation.viewmodel.CreateAlbumViewModel
import com.example.vinilosapp.ui.base.BaseActivity

class CreateAlbumActivity : BaseActivity() {

    private lateinit var binding: ActivityCreateAlbumBinding

    private val viewModel: CreateAlbumViewModel by viewModels()

    private var performerList: List<Performer> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateAlbumBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val btnNavIcon =
            binding.toolbar.root.findViewById<ImageView>(R.id.btnNavIcon)

        val refreshBtn =
            binding.toolbar.root.findViewById<ImageView>(R.id.refreshButton)

        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)

        btnNavIcon.setOnClickListener {
            finish()
        }

        refreshBtn.visibility = View.GONE

        setupStaticSpinners()
        setupListeners()
        observeViewModel()

        setupBottomNav(0)

        viewModel.loadMusicians()
    }

    private fun setupStaticSpinners() {

        val genres = listOf(
            "Classical",
            "Salsa",
            "Rock",
            "Folk"
        )

        binding.spGenre.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            genres
        )

        val labels = listOf(
            "Sony Music",
            "EMI",
            "Discos Fuentes",
            "Elektra",
            "Fania Records"
        )

        binding.spRecordLabel.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            labels
        )
    }

    private fun setupListeners() {

        binding.btnCreateAlbum.setOnClickListener {

            val selectedPerformerId =
                if (performerList.isNotEmpty()) {
                    performerList.getOrNull(
                        binding.spArtist.selectedItemPosition
                    )?.id
                } else {
                    null
                }

            viewModel.createAlbum(
                CreateAlbumRequest(
                    name = binding.etAlbumName.text
                        .toString()
                        .trim(),

                    cover = binding.etCoverUrl.text
                        .toString()
                        .trim(),

                    releaseDate = binding.etReleaseDate.text
                        .toString()
                        .trim(),

                    description = binding.etDescription.text
                        .toString()
                        .trim(),

                    genre = binding.spGenre.selectedItem.toString(),

                    recordLabel = binding.spRecordLabel.selectedItem.toString()
                ),
                selectedPerformerId
            )
        }
    }

    private fun observeViewModel() {

        viewModel.performers.observe(this) { performers ->

            performerList = performers

            val names = performers.map { it.name }

            binding.spArtist.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                names
            )
        }

        viewModel.createState.observe(this) { state ->

            when (state) {

                is CreateAlbumUiState.Loading -> {

                    binding.btnCreateAlbum.isEnabled = false
                    binding.btnCreateAlbum.text = "Creando..."
                }

                is CreateAlbumUiState.Success -> {

                    Toast.makeText(
                        this,
                        "¡Álbum creado!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        MainActivity::class.java
                    )

                    intent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intent)

                    finish()
                }

                is CreateAlbumUiState.Error -> {

                    binding.btnCreateAlbum.isEnabled = true

                    binding.btnCreateAlbum.text =
                        "ARCHIVE ALBUM"

                    Toast.makeText(
                        this,
                        state.message ?: "Error al crear álbum",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is CreateAlbumUiState.ValidationError -> {

                    binding.btnCreateAlbum.isEnabled = true

                    binding.btnCreateAlbum.text =
                        "ARCHIVE ALBUM"

                    val e = state.errors

                    e.name?.let {
                        binding.etAlbumName.error = it
                    }

                    e.cover?.let {
                        binding.etCoverUrl.error = it
                    }

                    e.releaseDate?.let {
                        binding.etReleaseDate.error = it
                    }

                    e.description?.let {
                        binding.etDescription.error = it
                    }

                    Toast.makeText(
                        this,
                        "Revisa los campos del formulario",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is CreateAlbumUiState.Idle -> {}
            }
        }
    }
}