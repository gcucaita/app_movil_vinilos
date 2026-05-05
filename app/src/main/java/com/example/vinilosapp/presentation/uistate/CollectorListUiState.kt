package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Collector

sealed interface CollectorListUiState {
    data object Loading : CollectorListUiState
    data class Success(val collectors: List<Collector>) : CollectorListUiState
    data class Error(val message: String? = null) : CollectorListUiState
}
