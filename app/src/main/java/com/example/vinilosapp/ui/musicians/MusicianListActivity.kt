package com.example.vinilosapp.ui.musicians

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.MainActivity
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.MusicianListUiState
import com.example.vinilosapp.presentation.viewmodel.MusicianListViewModel
import com.example.vinilosapp.ui.collectors.CollectorListActivity

class MusicianListActivity : AppCompatActivity() {

    private lateinit var viewModel: MusicianListViewModel
    private lateinit var adapter: MusicianListAdapter
    private var allMusicians: List<Performer> = emptyList()
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_musician_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contentContainer)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Toolbar
        val toolbar = findViewById<View>(R.id.toolbar)
        val btnNavIcon = toolbar.findViewById<ImageView>(R.id.btnNavIcon)
        val refreshBtn = toolbar.findViewById<ImageView>(R.id.refreshButton)
        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)
        btnNavIcon.setOnClickListener { finish() }
        refreshBtn.setOnClickListener {
            viewModel.loadMusicians()
        }

        // RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.musiciansRecyclerView)
        val loadingIndicator = findViewById<View>(R.id.loadingIndicator)
        val errorText = findViewById<TextView>(R.id.errorText)
        val searchInput = findViewById<EditText>(R.id.searchInput)

        adapter = MusicianListAdapter { musician ->
            val intent = Intent(this, MusicianDetailActivity::class.java)
            intent.putExtra("musicianId", musician.id)
            startActivity(intent)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MusicianListActivity)
            adapter = this@MusicianListActivity.adapter
        }

        viewModel = ViewModelProvider(this)[MusicianListViewModel::class.java]

        viewModel.uiState.observe(this) { state ->
            when (state) {
                is MusicianListUiState.Loading -> {
                    loadingIndicator.visibility = View.VISIBLE
                    errorText.visibility = View.GONE
                }
                is MusicianListUiState.Success -> {
                    allMusicians = state.musicians
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.GONE
                    applyFilter()
                }
                is MusicianListUiState.Error -> {
                    loadingIndicator.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    Toast.makeText(this, state.message ?: "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
                applyFilter()
            }
        })

        // BottomNav
        findViewById<TextView>(R.id.navAlbums).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        findViewById<TextView>(R.id.navCollectors).setOnClickListener {
            startActivity(Intent(this, CollectorListActivity::class.java))
            finish()
        }

        viewModel.loadMusicians()
    }

    private fun applyFilter() {
        val filtered = if (searchQuery.isEmpty()) {
            allMusicians
        } else {
            allMusicians.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
        adapter.submitList(filtered)
    }
}