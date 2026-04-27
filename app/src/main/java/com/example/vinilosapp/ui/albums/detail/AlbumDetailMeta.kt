package com.example.vinilosapp.ui.albums.detail

import com.example.vinilosapp.domain.model.Performer

/**
 * Helpers de formato del detalle del álbum (HU2).
 */

fun formatAlbumDetailDescription(description: String?, placeholder: String): String =
    description?.takeIf { it.isNotBlank() } ?: placeholder

fun formatAlbumDetailRecordLabel(recordLabel: String?): String =
    recordLabel?.takeIf { it.isNotBlank() }.orEmpty()

fun formatAlbumDetailArtistName(performers: List<Performer>?, unknownLabel: String): String =
    performers?.firstOrNull()?.name ?: unknownLabel

fun formatAlbumDetailYear(releaseDate: String?): String =
    releaseDate?.take(4)?.takeIf { it.length == 4 && it.all(Char::isDigit) }.orEmpty()
