package com.example.vinilosapp.ui.albums.list

import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Performer

fun formatAlbumListMetaText(album: Album): String {
    val year = album.releaseDate?.take(4)?.takeIf { it.all(Char::isDigit) } ?: "----"
    val label = album.recordLabel?.uppercase()?.ifBlank { "N/A" } ?: "N/A"
    return "$year · $label"
}

/** Devuelve el primer intérprete o [unknownLabel] si la lista es nula/vacía. */
fun formatAlbumListArtistName(performers: List<Performer>?, unknownLabel: String): String =
    performers?.firstOrNull()?.name ?: unknownLabel