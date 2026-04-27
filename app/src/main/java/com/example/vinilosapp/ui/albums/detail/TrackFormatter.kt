package com.example.vinilosapp.ui.albums.detail

/**
 * Helpers de formato para el listado de pistas (HU2).
 */

fun formatTrackNumber(position: Int): String {
    val displayNumber = position + 1
    return displayNumber.toString().padStart(2, '0')
}

fun formatTrackDuration(duration: String?): String =
    duration?.takeIf { it.isNotBlank() }.orEmpty()
