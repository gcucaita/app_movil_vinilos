package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.presentation.uistate.AlbumListUiState
import kotlinx.coroutines.launch

class AlbumListViewModel : ViewModel() {

    private val albumRepository: AlbumRepository = AlbumRepository()

    private val _uiState = MutableLiveData<AlbumListUiState>(AlbumListUiState.Loading)
    val uiState: LiveData<AlbumListUiState> = _uiState

    fun loadAlbums() {
        _uiState.value = AlbumListUiState.Loading

        viewModelScope.launch {
            val albums = albumRepository.getAllAlbums()
            _uiState.value = if (albums != null) {
                AlbumListUiState.Success(albums)
            } else {
                AlbumListUiState.Error("Unable to load albums")
            }
        }
    }
}
