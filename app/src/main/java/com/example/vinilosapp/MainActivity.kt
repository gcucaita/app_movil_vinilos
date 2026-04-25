package com.example.vinilosapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.R
import com.example.vinilosapp.presentation.uistate.AlbumListUiState
import com.example.vinilosapp.presentation.viewmodel.AlbumListViewModel
import com.example.vinilosapp.ui.albums.list.AlbumListAdapter
import android.content.Intent
import com.example.vinilosapp.ui.albums.detail.AlbumDetailActivity
class MainActivity : AppCompatActivity() {

    private lateinit var albumListViewModel: AlbumListViewModel
    private lateinit var albumListAdapter: AlbumListAdapter
    private lateinit var refreshButton: TextView
    private var refreshRotationAnimator: ObjectAnimator? = null

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
                    albumListAdapter.submitList(state.albums)
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
            refreshButton.rotation,
            refreshButton.rotation + 360f
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
