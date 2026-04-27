package com.example.vinilosapp.ui.albums.detail

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias del formato del listado de pistas (HU2).
 * Cubre formatTrackNumber y formatTrackDuration
 */
class TrackFormatterTest {

    @Test
    fun `formatTrackNumber convierte la primera pista en 01`() {
        assertEquals("01", formatTrackNumber(0))
    }

    @Test
    fun `formatTrackNumber pad-left con cero hasta dos digitos`() {
        assertEquals("02", formatTrackNumber(1))
        assertEquals("09", formatTrackNumber(8))
    }

    @Test
    fun `formatTrackNumber respeta dos digitos sin padding extra`() {
        assertEquals("10", formatTrackNumber(9))
        assertEquals("99", formatTrackNumber(98))
    }

    @Test
    fun `formatTrackNumber no trunca cuando hay mas de dos digitos`() {
        assertEquals("100", formatTrackNumber(99))
        assertEquals("123", formatTrackNumber(122))
    }

    @Test
    fun `formatTrackDuration devuelve cadena vacia cuando es nula`() {
        assertEquals("", formatTrackDuration(null))
    }

    @Test
    fun `formatTrackDuration devuelve cadena vacia cuando esta en blanco`() {
        assertEquals("", formatTrackDuration("   "))
    }

    @Test
    fun `formatTrackDuration devuelve cadena vacia cuando es vacia`() {
        assertEquals("", formatTrackDuration(""))
    }

    @Test
    fun `formatTrackDuration devuelve la duracion tal cual cuando tiene contenido`() {
        assertEquals("3:45", formatTrackDuration("3:45"))
        assertEquals("12:07", formatTrackDuration("12:07"))
    }
}
