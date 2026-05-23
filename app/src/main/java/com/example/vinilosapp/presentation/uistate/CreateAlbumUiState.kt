package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Album

sealed interface CreateAlbumUiState {
    data object Idle : CreateAlbumUiState
    data class ValidationError(val errors: CreateAlbumValidationErrors) : CreateAlbumUiState
    data object Loading : CreateAlbumUiState
    data class Success(val album: Album) : CreateAlbumUiState
    data class Error(val message: String) : CreateAlbumUiState
}

data class CreateAlbumValidationErrors(
    val name: String? = null,
    val cover: String? = null,
    val releaseDate: String? = null,
    val description: String? = null,
    val genre: String? = null,
    val recordLabel: String? = null,
) {
    fun hasErrors(): Boolean = listOf(name, cover, releaseDate, description, genre, recordLabel).any { it != null }
}