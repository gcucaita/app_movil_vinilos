package com.example.vinilosapp.presentation.viewmodel

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CreateAlbumFormValidatorTest {

    @Test
    fun `validate devuelve errores cuando faltan campos requeridos`() {
        val errors = CreateAlbumFormValidator.validate(
            name = " ",
            cover = " ",
            releaseDate = " ",
            description = " ",
            genre = " ",
            recordLabel = " ",
        )

        assertTrue(errors.hasErrors())
        assertTrue(errors.name != null)
        assertTrue(errors.cover != null)
        assertTrue(errors.releaseDate != null)
        assertTrue(errors.description != null)
        assertTrue(errors.genre != null)
        assertTrue(errors.recordLabel != null)
    }

    @Test
    fun `validate marca error cuando cover no es URL valida`() {
        val errors = CreateAlbumFormValidator.validate(
            name = "Buscando America",
            cover = "not-a-url",
            releaseDate = "1984-08-01T05:00:00.000Z",
            description = "Descripcion",
            genre = "Salsa",
            recordLabel = "Elektra",
        )

        assertTrue(errors.cover != null)
    }

    @Test
    fun `validate marca error cuando releaseDate no es ISO 8601`() {
        val errors = CreateAlbumFormValidator.validate(
            name = "Buscando America",
            cover = "https://example.com/cover.jpg",
            releaseDate = "01-08-1984",
            description = "Descripcion",
            genre = "Salsa",
            recordLabel = "Elektra",
        )

        assertTrue(errors.releaseDate != null)
    }

    @Test
    fun `validate no devuelve errores con datos validos`() {
        val errors = CreateAlbumFormValidator.validate(
            name = "Buscando America",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1984-08-01T05:00:00.000Z",
            description = "Descripcion",
            genre = "Salsa",
            recordLabel = "Elektra",
        )

        assertFalse(errors.hasErrors())
    }
}
