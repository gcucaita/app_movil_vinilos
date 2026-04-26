package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Album
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumListUiStateTest {

    @Test
    fun `Loading siempre es la misma instancia`() {
        val a: AlbumListUiState = AlbumListUiState.Loading
        val b: AlbumListUiState = AlbumListUiState.Loading

        assertEquals(a, b)
    }

    @Test
    fun `Success guarda la lista de albumes recibida`() {
        val albums = listOf(albumFixture())

        val state = AlbumListUiState.Success(albums)

        assertEquals(albums, state.albums)
    }

    @Test
    fun `Error tiene mensaje nulo por defecto`() {
        val state = AlbumListUiState.Error()

        assertNull(state.message)
    }

    @Test
    fun `Error con mensaje guarda el valor recibido`() {
        val state = AlbumListUiState.Error("Sin conexion")

        assertEquals("Sin conexion", state.message)
    }

    @Test
    fun `Success con listas distintas no son iguales`() {
        val a = AlbumListUiState.Success(listOf(albumFixture()))
        val b = AlbumListUiState.Success(emptyList())

        assertNotEquals(a, b)
    }

    private fun albumFixture(): Album = Album(
        id = 100,
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