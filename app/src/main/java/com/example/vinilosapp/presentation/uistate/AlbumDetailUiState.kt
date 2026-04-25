package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Album

sealed class AlbumDetailUiState {
    object Loading : AlbumDetailUiState()
    data class Success(val album: Album) : AlbumDetailUiState()
    data class Error(val message: String) : AlbumDetailUiState()
}