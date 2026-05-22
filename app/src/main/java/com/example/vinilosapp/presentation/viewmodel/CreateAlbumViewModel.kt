package com.example.vinilosapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.data.repository.MusicianRepository
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.presentation.uistate.AddTrackUiState
import com.example.vinilosapp.presentation.uistate.CreateAlbumUiState
import com.example.vinilosapp.presentation.uistate.CreateAlbumValidationErrors
import kotlinx.coroutines.launch

class CreateAlbumViewModel : ViewModel() {

    private val albumRepository = AlbumRepository()
    private val musicianRepository = MusicianRepository()

    private val _createState = MutableLiveData<CreateAlbumUiState>(CreateAlbumUiState.Idle)
    val createState: LiveData<CreateAlbumUiState> = _createState

    private val _trackState = MutableLiveData<AddTrackUiState>(AddTrackUiState.Idle)
    val trackState: LiveData<AddTrackUiState> = _trackState

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _performers = MutableLiveData<List<Performer>>(emptyList())
    val performers: LiveData<List<Performer>> = _performers

    var createdAlbumId: Int? = null

    fun loadMusicians() {
        viewModelScope.launch {
            val musicians = musicianRepository.getAllMusicians() ?: emptyList()
            val bands = musicianRepository.getAllBands() ?: emptyList()
            _performers.value = musicians + bands
        }
    }

    fun createAlbum(request: CreateAlbumRequest, performerId: Int?) {
        Log.d("CREATE_ALBUM_VM", "REQUEST: $request")

        val errors = validate(request)
        if (errors.hasErrors()) {
            _createState.value = CreateAlbumUiState.ValidationError(errors)
            return
        }

        _createState.value = CreateAlbumUiState.Loading

        viewModelScope.launch {
            try {
                val result = albumRepository.createAlbum(request)

                if (result != null) {
                    createdAlbumId = result.id

                    if (performerId != null) {
                        albumRepository.addPerformerToAlbum(result.id, performerId)
                    }

                    _createState.value = CreateAlbumUiState.Success(result)
                } else {
                    _createState.value = CreateAlbumUiState.Error("No se pudo crear el álbum")
                }
            } catch (e: Exception) {
                _createState.value = CreateAlbumUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun addTrack(name: String, duration: String) {
        val albumId = createdAlbumId ?: run {
            _trackState.value = AddTrackUiState.Error("Primero crea el álbum")
            return
        }

        _trackState.value = AddTrackUiState.Loading

        viewModelScope.launch {
            val track = albumRepository.addTrack(albumId, CreateTrackRequest(name, duration))

            if (track != null) {
                val current = _tracks.value?.toMutableList() ?: mutableListOf()
                current.add(track)
                _tracks.value = current
                _trackState.value = AddTrackUiState.Success(track)
            } else {
                _trackState.value = AddTrackUiState.Error("No se pudo agregar el track")
            }
        }
    }

    private fun validate(request: CreateAlbumRequest): CreateAlbumValidationErrors {
        return CreateAlbumValidationErrors(
            name = if (request.name.isBlank()) "El nombre es requerido" else null,
            cover = if (request.cover.isBlank()) "La URL de la portada es requerida" else null,
            releaseDate =
                if (request.releaseDate.isBlank()) "La fecha es requerida"
                else if (!request.releaseDate.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
                    "Formato inválido (YYYY-MM-DD)"
                else null,
            description = if (request.description.isBlank()) "La descripción es requerida" else null,
            genre = if (request.genre.isBlank()) "El género es requerido" else null,
            recordLabel = if (request.recordLabel.isBlank()) "El sello es requerido" else null
        )
    }
}