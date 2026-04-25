package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.presentation.uistate.AlbumDetailUiState
import kotlinx.coroutines.launch

class AlbumDetailViewModel : ViewModel() {

    private val albumRepository: AlbumRepository = AlbumRepository()

    private val _uiState = MutableLiveData<AlbumDetailUiState>(AlbumDetailUiState.Loading)
    val uiState: LiveData<AlbumDetailUiState> = _uiState

    fun loadAlbum(id: Int) {
        _uiState.value = AlbumDetailUiState.Loading
        viewModelScope.launch {
            val album = albumRepository.getAlbum(id)
            _uiState.value = if (album != null) {
                AlbumDetailUiState.Success(album)
            } else {
                AlbumDetailUiState.Error("No se pudo cargar el álbum")
            }
        }
    }
}