package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.CollectorRepository
import com.example.vinilosapp.presentation.uistate.CollectorDetailUiState
import kotlinx.coroutines.launch

class CollectorDetailViewModel : ViewModel() {

    private val repository = CollectorRepository()

    private val _uiState = MutableLiveData<CollectorDetailUiState>(CollectorDetailUiState.Loading)
    val uiState: LiveData<CollectorDetailUiState> = _uiState

    fun loadCollector(id: Int) {
        _uiState.value = CollectorDetailUiState.Loading
        viewModelScope.launch {
            val collector = repository.getCollector(id)
            _uiState.value = if (collector != null) {
                CollectorDetailUiState.Success(collector)
            } else {
                CollectorDetailUiState.Error("No se pudo cargar el coleccionista")
            }
        }
    }
}