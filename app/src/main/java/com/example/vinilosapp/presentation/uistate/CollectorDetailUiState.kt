package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Collector

sealed interface CollectorDetailUiState {
    data object Loading : CollectorDetailUiState
    data class Success(val collector: Collector) : CollectorDetailUiState
    data class Error(val message: String? = null) : CollectorDetailUiState
}