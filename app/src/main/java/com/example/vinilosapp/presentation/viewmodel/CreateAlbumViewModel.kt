package com.example.vinilosapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.presentation.uistate.CreateAlbumUiState
import kotlinx.coroutines.launch

class CreateAlbumViewModel(
    private val albumRepository: AlbumRepository = AlbumRepository(),
) : ViewModel() {

    private val _uiState = MutableLiveData<CreateAlbumUiState>(CreateAlbumUiState.Idle)
    val uiState: LiveData<CreateAlbumUiState> = _uiState

    fun createAlbum(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String,
    ) {
        if (_uiState.value is CreateAlbumUiState.Loading) return

        val errors = CreateAlbumFormValidator.validate(
            name = name,
            cover = cover,
            releaseDate = releaseDate,
            description = description,
            genre = genre,
            recordLabel = recordLabel,
        )

        if (errors.hasErrors()) {
            _uiState.value = CreateAlbumUiState.ValidationError(errors)
            return
        }

        _uiState.value = CreateAlbumUiState.Loading
        viewModelScope.launch {
            val result = albumRepository.createAlbum(
                CreateAlbumRequest(
                    name = name.trim(),
                    cover = cover.trim(),
                    releaseDate = releaseDate.trim(),
                    description = description.trim(),
                    genre = genre.trim(),
                    recordLabel = recordLabel.trim(),
                )
            )

            _uiState.value = result.fold(
                onSuccess = { CreateAlbumUiState.Success(it) },
                onFailure = {
                    CreateAlbumUiState.Error(it.message ?: "No se pudo crear el album")
                }
            )
        }
    }

    fun clearState() {
        _uiState.value = CreateAlbumUiState.Idle
    }
}
