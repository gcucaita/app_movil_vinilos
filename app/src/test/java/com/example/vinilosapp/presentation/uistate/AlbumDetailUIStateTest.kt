package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Album
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias del estado de UI del detalle de álbum — HU2.
 *
 * Cubre: Loading, Success y Error.
 */
class AlbumDetailUiStateTest {

    @Test
    fun `Loading siempre es la misma instancia singleton`() {
        val a: AlbumDetailUiState = AlbumDetailUiState.Loading
        val b: AlbumDetailUiState = AlbumDetailUiState.Loading

        assertSame(a, b)
        assertEquals(a, b)
    }

    @Test
    fun `Success guarda el album recibido`() {
        val album = albumFixture()

        val state = AlbumDetailUiState.Success(album)

        assertEquals(album, state.album)
    }

    @Test
    fun `Success con el mismo album es igual`() {
        val album = albumFixture()

        val a = AlbumDetailUiState.Success(album)
        val b = AlbumDetailUiState.Success(album)

        assertEquals(a, b)
    }

    @Test
    fun `Success con albumes distintos no son iguales`() {
        val a = AlbumDetailUiState.Success(albumFixture(id = 100))
        val b = AlbumDetailUiState.Success(albumFixture(id = 200))

        assertNotEquals(a, b)
    }

    @Test
    fun `Error guarda el mensaje recibido`() {
        val state = AlbumDetailUiState.Error("No se pudo cargar el álbum")

        assertEquals("No se pudo cargar el álbum", state.message)
    }

    @Test
    fun `Error con mensajes distintos no son iguales`() {
        val a = AlbumDetailUiState.Error("Sin conexion")
        val b = AlbumDetailUiState.Error("Timeout")

        assertNotEquals(a, b)
    }

    @Test
    fun `Loading Success y Error son ramas distintas del sealed class`() {
        val loading: AlbumDetailUiState = AlbumDetailUiState.Loading
        val success: AlbumDetailUiState = AlbumDetailUiState.Success(albumFixture())
        val error: AlbumDetailUiState = AlbumDetailUiState.Error("oops")

        assertTrue(loading is AlbumDetailUiState.Loading)
        assertTrue(success is AlbumDetailUiState.Success)
        assertTrue(error is AlbumDetailUiState.Error)
        assertNotEquals(loading, success)
        assertNotEquals(success, error)
        assertNotEquals(loading, error)
    }

    private fun albumFixture(id: Int = 100): Album = Album(
        id = id,
        name = "Buscando America",
        cover = "https://example.com/cover.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = null,
        genre = "Salsa",
        recordLabel = "Elektra",
    )
}
