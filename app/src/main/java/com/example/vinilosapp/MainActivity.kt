package com.example.vinilosapp

import android.os.Bundle
import android.view.View
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

class MainActivity : AppCompatActivity() {

    private lateinit var albumListViewModel: AlbumListViewModel
    private lateinit var albumListAdapter: AlbumListAdapter

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
        val refreshButton = findViewById<TextView>(R.id.refreshButton)

        albumListAdapter = AlbumListAdapter()
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
                }

                is AlbumListUiState.Success -> {
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.GONE
                    albumListAdapter.submitList(state.albums)
                }

                is AlbumListUiState.Error -> {
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    Toast.makeText(this, getString(R.string.albums_error_toast), Toast.LENGTH_SHORT).show()
                }
            }
        }

        refreshButton.setOnClickListener {
            albumListViewModel.loadAlbums()
        }

        albumListViewModel.loadAlbums()
    }
}
