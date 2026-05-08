package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.CollectorRepository
import com.example.vinilosapp.presentation.uistate.CollectorListUiState
import kotlinx.coroutines.launch

class CollectorListViewModel(
    private val collectorRepository: CollectorRepository = CollectorRepository()
) : ViewModel() {

    private val _uiState = MutableLiveData<CollectorListUiState>(CollectorListUiState.Loading)
    val uiState: LiveData<CollectorListUiState> = _uiState

    fun loadCollectors() {
        _uiState.value = CollectorListUiState.Loading

        viewModelScope.launch {
            val collectors = collectorRepository.getAllCollectors()
            _uiState.value = if (collectors != null) {
                CollectorListUiState.Success(collectors)
            } else {
                CollectorListUiState.Error("Unable to load collectors")
            }
        }
    }
}
