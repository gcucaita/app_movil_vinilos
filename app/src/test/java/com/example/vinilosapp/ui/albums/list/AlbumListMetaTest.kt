package com.example.vinilosapp.ui.albums.list

import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Performer
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias del formating del catálogo (HU1).
 *
 * Cubre todas las ramas del placeholder de año/sello y del nombre del intérprete.
 */
class AlbumListMetaTest {

    @Test
    fun `meta usa anio y sello en mayusculas cuando hay datos`() {
        val album = albumFixture(releaseDate = "1984-08-01T05:00:00.000Z", recordLabel = "Elektra")

        assertEquals("1984 · ELEKTRA", formatAlbumListMetaText(album))
    }

    @Test
    fun `meta usa guiones cuando releaseDate es nulo`() {
        val album = albumFixture(releaseDate = null, recordLabel = "Fania")

        assertEquals("---- · FANIA", formatAlbumListMetaText(album))
    }

    @Test
    fun `meta usa guiones cuando los primeros 4 caracteres no son digitos`() {
        val album = albumFixture(releaseDate = "abcd-08-01", recordLabel = "Sony")

        assertEquals("---- · SONY", formatAlbumListMetaText(album))
    }

    @Test
    fun `meta usa NA cuando recordLabel es nulo`() {
        val album = albumFixture(releaseDate = "2001-01-01", recordLabel = null)

        assertEquals("2001 · N/A", formatAlbumListMetaText(album))
    }

    @Test
    fun `meta usa NA cuando recordLabel esta en blanco`() {
        val album = albumFixture(releaseDate = "2001-01-01", recordLabel = "   ")

        assertEquals("2001 · N/A", formatAlbumListMetaText(album))
    }

    @Test
    fun `meta usa todos los placeholders cuando ambos campos faltan`() {
        val album = albumFixture(releaseDate = null, recordLabel = null)

        assertEquals("---- · N/A", formatAlbumListMetaText(album))
    }

    @Test
    fun `artist devuelve nombre del primer interprete`() {
        val performers = listOf(
            Performer(id = 1, name = "Ruben Blades", image = null, description = null, birthDate = null),
            Performer(id = 2, name = "Willie Colon", image = null, description = null, birthDate = null),
        )

        assertEquals("Ruben Blades", formatAlbumListArtistName(performers, "Desconocido"))
    }

    @Test
    fun `artist devuelve etiqueta cuando lista es nula`() {
        assertEquals("Desconocido", formatAlbumListArtistName(null, "Desconocido"))
    }

    @Test
    fun `artist devuelve etiqueta cuando lista esta vacia`() {
        assertEquals("Desconocido", formatAlbumListArtistName(emptyList(), "Desconocido"))
    }

    private fun albumFixture(
        releaseDate: String?,
        recordLabel: String?,
    ): Album = Album(
        id = 100,
        name = "Buscando America",
        cover = "https://example.com/cover.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = releaseDate,
        description = null,
        genre = "Salsa",
        recordLabel = recordLabel,
    )
}