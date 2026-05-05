package com.example.vinilosapp.presentation.uistate

import com.example.vinilosapp.domain.model.Performer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MusicianListUiStateTest {

    @Test
    fun `Loading siempre es la misma instancia`() {
        val a: MusicianListUiState = MusicianListUiState.Loading
        val b: MusicianListUiState = MusicianListUiState.Loading

        assertEquals(a, b)
    }

    @Test
    fun `Success guarda la lista de musicians recibida`() {
        val musicians = listOf(musicianFixture())

        val state = MusicianListUiState.Success(musicians)

        assertEquals(musicians, state.musicians)
    }

    @Test
    fun `Error tiene mensaje nulo por defecto`() {
        val state = MusicianListUiState.Error()

        assertNull(state.message)
    }

    @Test
    fun `Error con mensaje guarda el valor recibido`() {
        val state = MusicianListUiState.Error("Sin conexion")

        assertEquals("Sin conexion", state.message)
    }

    @Test
    fun `Success con listas distintas no son iguales`() {
        val a = MusicianListUiState.Success(listOf(musicianFixture()))
        val b = MusicianListUiState.Success(emptyList())

        assertNotEquals(a, b)
    }

    private fun musicianFixture(): Performer = Performer(
        id = 100,
        name = "Ruben Blades",
        image = "https://example.com/cover.jpg",
        description = "Cantante panameno",
        birthDate = "1948-07-16T05:00:00.000Z",
    )
}
