package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Track

sealed interface AddTrackUiState {
    data object Idle : AddTrackUiState
    data object Loading : AddTrackUiState
    data class Success(val track: Track) : AddTrackUiState
    data class Error(val message: String? = null) : AddTrackUiState
}