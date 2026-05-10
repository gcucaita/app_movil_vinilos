package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Performer

sealed interface MusicianDetailUiState {
    data object Loading : MusicianDetailUiState
    data class Success(val musician: Performer) : MusicianDetailUiState
    data class Error(val message: String? = null) : MusicianDetailUiState
}