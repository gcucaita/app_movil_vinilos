package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Album

sealed interface AlbumListUiState {
    data object Loading : AlbumListUiState
    data class Success(val albums: List<Album>) : AlbumListUiState
    data class Error(val message: String? = null) : AlbumListUiState
}
