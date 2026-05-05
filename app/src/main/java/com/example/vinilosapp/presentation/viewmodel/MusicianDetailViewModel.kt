package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.MusicianRepository
import com.example.vinilosapp.presentation.uistate.MusicianDetailUiState
import kotlinx.coroutines.launch

class MusicianDetailViewModel : ViewModel() {

    private val repository = MusicianRepository()

    private val _uiState = MutableLiveData<MusicianDetailUiState>(MusicianDetailUiState.Loading)
    val uiState: LiveData<MusicianDetailUiState> = _uiState

    fun loadMusician(id: Int) {
        _uiState.value = MusicianDetailUiState.Loading
        viewModelScope.launch {
            val musician = repository.getMusician(id)
            _uiState.value = if (musician != null) {
                MusicianDetailUiState.Success(musician)
            } else {
                MusicianDetailUiState.Error("No se pudo cargar el artista")
            }
        }
    }
}