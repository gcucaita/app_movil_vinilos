package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.MusicianRepository
import com.example.vinilosapp.presentation.uistate.MusicianListUiState
import kotlinx.coroutines.launch

class MusicianListViewModel : ViewModel() {

    private val musicianRepository: MusicianRepository = MusicianRepository()

    private val _uiState = MutableLiveData<MusicianListUiState>(MusicianListUiState.Loading)
    val uiState: LiveData<MusicianListUiState> = _uiState

    fun loadMusicians() {
        _uiState.value = MusicianListUiState.Loading

        viewModelScope.launch {
            val musicians = musicianRepository.getAllMusicians()
            _uiState.value = if (musicians != null) {
                MusicianListUiState.Success(musicians)
            } else {
                MusicianListUiState.Error("Unable to load musicians")
            }
        }
    }
}
