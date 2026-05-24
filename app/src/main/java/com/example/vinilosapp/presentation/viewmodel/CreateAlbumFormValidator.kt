package com.example.vinilosapp.presentation.viewmodel

import com.example.vinilosapp.presentation.uistate.CreateAlbumValidationErrors
import java.net.URI
import java.time.Instant

object CreateAlbumFormValidator {

    fun validate(
        name: String,
        cover: String,
        releaseDate: String,
        description: String,
        genre: String,
        recordLabel: String,
    ): CreateAlbumValidationErrors {
        val trimmedName = name.trim()
        val trimmedCover = cover.trim()
        val trimmedReleaseDate = releaseDate.trim()
        val trimmedDescription = description.trim()
        val trimmedGenre = genre.trim()
        val trimmedRecordLabel = recordLabel.trim()

        return CreateAlbumValidationErrors(
            name = if (trimmedName.isBlank()) "El nombre es obligatorio" else null,
            cover = when {
                trimmedCover.isBlank() -> "La portada es obligatoria"
                !isValidUrl(trimmedCover) -> "La portada debe ser una URL valida"
                else -> null
            },
            releaseDate = when {
                trimmedReleaseDate.isBlank() -> "La fecha es obligatoria"
                !isValidIsoInstant(trimmedReleaseDate) -> "La fecha debe estar en formato ISO 8601"
                else -> null
            },
            description = if (trimmedDescription.isBlank()) "La descripcion es obligatoria" else null,
            genre = if (trimmedGenre.isBlank()) "El genero es obligatorio" else null,
            recordLabel = if (trimmedRecordLabel.isBlank()) "La disquera es obligatoria" else null,
        )
    }

    private fun isValidUrl(value: String): Boolean = runCatching {
        val uri = URI(value)
        uri.scheme in setOf("http", "https") && !uri.host.isNullOrBlank()
    }.getOrDefault(false)

    private fun isValidIsoInstant(value: String): Boolean = runCatching {
        Instant.parse(value)
    }.isSuccess
}
