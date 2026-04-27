package com.example.vinilosapp.ui.albums.detail

import com.example.vinilosapp.domain.model.Performer
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias del formato del detalle del álbum (HU2).
 *
 * Cubre todos los helpers:
 */
class AlbumDetailMetaTest {

    @Test
    fun `description devuelve placeholder cuando es nula`() {
        assertEquals("Sin descripción", formatAlbumDetailDescription(null, "Sin descripción"))
    }

    @Test
    fun `description devuelve placeholder cuando esta en blanco`() {
        assertEquals("Sin descripción", formatAlbumDetailDescription("   ", "Sin descripción"))
    }

    @Test
    fun `description devuelve el texto cuando tiene contenido`() {
        assertEquals(
            "Album conceptual de salsa",
            formatAlbumDetailDescription("Album conceptual de salsa", "Sin descripción"),
        )
    }

    @Test
    fun `recordLabel devuelve cadena vacia cuando es nulo`() {
        assertEquals("", formatAlbumDetailRecordLabel(null))
    }

    @Test
    fun `recordLabel devuelve cadena vacia cuando esta en blanco`() {
        assertEquals("", formatAlbumDetailRecordLabel("   "))
    }

    @Test
    fun `recordLabel devuelve el sello tal cual cuando tiene contenido`() {
        assertEquals("Elektra", formatAlbumDetailRecordLabel("Elektra"))
    }

    @Test
    fun `artist devuelve nombre del primer interprete`() {
        val performers = listOf(
            Performer(id = 1, name = "Ruben Blades", image = null, description = null, birthDate = null),
            Performer(id = 2, name = "Willie Colon", image = null, description = null, birthDate = null),
        )

        assertEquals("Ruben Blades", formatAlbumDetailArtistName(performers, "Artista desconocido"))
    }

    @Test
    fun `artist devuelve placeholder cuando la lista es nula`() {
        assertEquals(
            "Artista desconocido",
            formatAlbumDetailArtistName(null, "Artista desconocido"),
        )
    }

    @Test
    fun `artist devuelve placeholder cuando la lista esta vacia`() {
        assertEquals(
            "Artista desconocido",
            formatAlbumDetailArtistName(emptyList(), "Artista desconocido"),
        )
    }

    @Test
    fun `year devuelve los cuatro digitos cuando releaseDate es valido`() {
        assertEquals("1984", formatAlbumDetailYear("1984-08-01T05:00:00.000Z"))
    }

    @Test
    fun `year devuelve cadena vacia cuando releaseDate es nulo`() {
        assertEquals("", formatAlbumDetailYear(null))
    }

    @Test
    fun `year devuelve cadena vacia cuando releaseDate es muy corto`() {
        assertEquals("", formatAlbumDetailYear("198"))
    }

    @Test
    fun `year devuelve cadena vacia cuando los primeros 4 caracteres no son digitos`() {
        assertEquals("", formatAlbumDetailYear("abcd-08-01"))
    }

    @Test
    fun `year devuelve cadena vacia cuando releaseDate esta vacio`() {
        assertEquals("", formatAlbumDetailYear(""))
    }
}
