package com.example.vinilosapp.ui.musicians

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vinilosapp.R
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.MusicianListUiState
import com.example.vinilosapp.presentation.viewmodel.MusicianListViewModel
import com.example.vinilosapp.ui.base.BaseActivity

class MusicianListActivity : BaseActivity() {

    private lateinit var viewModel: MusicianListViewModel
    private lateinit var adapter: MusicianListAdapter

    private var allMusicians: List<Performer> = emptyList()
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_musician_list)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.bottomNav)
        ) { view, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = navBarInsets.bottom
            }
            insets
        }

        setupToolbar()
        setupRecyclerView()
        setupSearch()

        setupBottomNav(1)

        viewModel =
            ViewModelProvider(this)[MusicianListViewModel::class.java]

        observeViewModel()

        viewModel.loadMusicians()
    }

    private fun setupToolbar() {

        val toolbar =
            findViewById<View>(R.id.toolbar)

        val btnNavIcon =
            toolbar.findViewById<ImageView>(R.id.btnNavIcon)

        val refreshBtn =
            toolbar.findViewById<ImageView>(R.id.refreshButton)

        btnNavIcon.setImageResource(R.drawable.outline_arrow_back_24)

        btnNavIcon.setOnClickListener {
            finish()
        }

        refreshBtn.setOnClickListener {
            viewModel.loadMusicians()
        }
    }

    private fun setupRecyclerView() {

        val recyclerView =
            findViewById<RecyclerView>(R.id.musiciansRecyclerView)

        adapter = MusicianListAdapter { musician ->

            val intent =
                Intent(this, MusicianDetailActivity::class.java)

            intent.putExtra("musicianId", musician.id)

            startActivity(intent)
        }

        recyclerView.apply {

            layoutManager =
                LinearLayoutManager(this@MusicianListActivity)

            adapter = this@MusicianListActivity.adapter
        }
    }

    private fun setupSearch() {

        val searchInput =
            findViewById<EditText>(R.id.searchInput)

        searchInput.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {

                searchQuery = s.toString()

                applyFilter()
            }
        })
    }

    private fun observeViewModel() {

        val loadingIndicator =
            findViewById<View>(R.id.loadingIndicator)

        val errorText =
            findViewById<TextView>(R.id.errorText)

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

                    Toast.makeText(
                        this,
                        state.message ?: "Error",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun applyFilter() {

        val filtered = if (searchQuery.isEmpty()) {

            allMusicians

        } else {

            allMusicians.filter {

                it.name.contains(
                    searchQuery,
                    ignoreCase = true
                )
            }
        }

        adapter.submitList(filtered)
    }
}