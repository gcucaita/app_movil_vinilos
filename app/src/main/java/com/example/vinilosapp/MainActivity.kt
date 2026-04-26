package com.example.vinilosapp

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.presentation.uistate.AlbumListUiState
import com.example.vinilosapp.presentation.viewmodel.AlbumListViewModel
import com.example.vinilosapp.ui.albums.detail.AlbumDetailActivity
import com.example.vinilosapp.ui.albums.list.AlbumListAdapter
import com.example.vinilosapp.domain.model.Album

class MainActivity : AppCompatActivity() {

    private lateinit var albumListViewModel: AlbumListViewModel
    private lateinit var albumListAdapter: AlbumListAdapter
    private lateinit var refreshButton: ImageView
    private var refreshRotationAnimator: ObjectAnimator? = null

    private lateinit var chipsContainer: LinearLayout

    private var allAlbums: List<Album> = emptyList()

    companion object {
        const val ALL_GENRES = "Todos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val albumsRecyclerView = findViewById<RecyclerView>(R.id.albumsRecyclerView)
        val loadingIndicator = findViewById<View>(R.id.loadingIndicator)
        val errorText = findViewById<TextView>(R.id.errorText)
        refreshButton = findViewById(R.id.refreshButton)
        chipsContainer = findViewById(R.id.chipsContainer)

        // Toolbar
        val toolbar = findViewById<View>(R.id.toolbar)
        val btnNavIcon = toolbar.findViewById<ImageView>(R.id.btnNavIcon)
        btnNavIcon.setImageResource(R.drawable.menu)

        albumListAdapter = AlbumListAdapter { album ->
            val intent = Intent(this, AlbumDetailActivity::class.java)
            intent.putExtra("albumId", album.id)
            startActivity(intent)
        }

        albumsRecyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = albumListAdapter
            setHasFixedSize(true)
        }

        albumListViewModel = ViewModelProvider(this)[AlbumListViewModel::class.java]

        albumListViewModel.uiState.observe(this) { state ->
            when (state) {
                is AlbumListUiState.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                    startRefreshLoadingAnimation()
                }

                is AlbumListUiState.Success -> {
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.GONE

                    allAlbums = state.albums

                    setupChips(allAlbums)
                    albumListAdapter.submitList(allAlbums)

                    stopRefreshLoadingAnimation()
                }

                is AlbumListUiState.Error -> {
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.albums_error_toast), Toast.LENGTH_SHORT).show()
                    stopRefreshLoadingAnimation()
                }
            }
        }

        refreshButton.setOnClickListener {
            animateRefreshTap()
            albumListViewModel.loadAlbums()
        }

        albumListViewModel.loadAlbums()
    }

    // -------------------------------
    // CHIPS DINÁMICOS
    // -------------------------------
    private fun setupChips(albums: List<Album>) {
        chipsContainer.removeAllViews()

        val genres = listOf(ALL_GENRES) +
                albums.mapNotNull { it.genre }.distinct()

        genres.forEachIndexed { index, genre ->
            val chip = TextView(this).apply {
                text = genre.uppercase()
                setPadding(36, 18, 36, 18)
                textSize = 13f

                setBackgroundResource(
                    if (index == 0) R.drawable.bg_chip_selected
                    else R.drawable.bg_chip_unselected
                )

                setTextColor(
                    if (index == 0) getColor(R.color.green_accent)
                    else getColor(R.color.chip_text)
                )

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.marginEnd = 16
                layoutParams = params

                setOnClickListener {
                    onChipSelected(this, genre)
                }
            }

            chipsContainer.addView(chip)
        }
    }

    private fun onChipSelected(selectedChip: TextView, genre: String) {
        for (i in 0 until chipsContainer.childCount) {
            val chip = chipsContainer.getChildAt(i) as TextView
            chip.setBackgroundResource(R.drawable.bg_chip_unselected)
            chip.setTextColor(getColor(R.color.chip_text))
        }

        selectedChip.setBackgroundResource(R.drawable.bg_chip_selected)
        selectedChip.setTextColor(getColor(R.color.green_accent))

        filterAlbums(genre)
    }

    private fun filterAlbums(genre: String) {
        val filtered = if (genre == ALL_GENRES) {
            allAlbums
        } else {
            allAlbums.filter { it.genre == genre }
        }

        albumListAdapter.submitList(filtered)
    }

    // -------------------------------
    // ANIMACIONES
    // -------------------------------
    private fun animateRefreshTap() {
        refreshButton.animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .setDuration(90)
            .withEndAction {
                refreshButton.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(120)
                    .start()
            }
            .start()
    }

    private fun startRefreshLoadingAnimation() {
        if (refreshRotationAnimator?.isRunning == true) return

        refreshRotationAnimator = ObjectAnimator.ofFloat(
            refreshButton,
            View.ROTATION,
            0f,
            360f
        ).apply {
            duration = 700
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
            start()
        }
    }

    private fun stopRefreshLoadingAnimation() {
        refreshRotationAnimator?.cancel()
        refreshRotationAnimator = null
        refreshButton.rotation = 0f
    }
}