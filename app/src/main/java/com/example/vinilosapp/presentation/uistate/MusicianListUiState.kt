package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Performer

sealed interface MusicianListUiState {
    data object Loading : MusicianListUiState
    data class Success(val musicians: List<Performer>) : MusicianListUiState
    data class Error(val message: String? = null) : MusicianListUiState
}
