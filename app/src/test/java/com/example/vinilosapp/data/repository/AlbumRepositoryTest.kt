package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.testutil.AlbumFixtures
import com.example.vinilosapp.testutil.FakeVinilosApiService
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pruebas unitarias HU01 - Consultar catálogo de álbumes
 */
@RunWith(RobolectricTestRunner::class)
class AlbumRepositoryTest {

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
    }

    @After
    fun tearDown() {
        CacheManager.clearAllCaches()
    }

    @Test
    fun `getAllAlbums devuelve lista cuando la API responde 200`() = runBlocking {
        val expected = AlbumFixtures.catalog()
        val repository = AlbumRepository(FakeVinilosApiService.success(expected))

        val result = repository.getAllAlbums()

        assertEquals(expected, result)
    }

    @Test
    fun `getAllAlbums devuelve null cuando la API lanza excepcion`() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.throwing(RuntimeException("network")))

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun `getAllAlbums devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.httpError(code = 500))

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun `getAllAlbums sirve desde cache en la segunda llamada`() = runBlocking {
        val expected = AlbumFixtures.catalog()
        val api = FakeVinilosApiService.success(expected)
        val repository = AlbumRepository(api)

        val first = repository.getAllAlbums()
        val second = repository.getAllAlbums()

        assertEquals(expected, first)
        assertEquals(expected, second)
        assertEquals(1, api.albumsCallCount)
    }

    @Test
    fun `getAllAlbums refresca despues de invalidar cache`() = runBlocking {
        val expected = AlbumFixtures.catalog()
        val api = FakeVinilosApiService.success(expected)
        val repository = AlbumRepository(api)

        repository.getAllAlbums()
        CacheManager.invalidateAlbumsListCache()
        repository.getAllAlbums()

        assertEquals(2, api.albumsCallCount)
    }

    @Test
    fun `getAlbum devuelve album cuando la API responde 200`() = runBlocking {
        val expected = AlbumFixtures.album(id = 100)
        val repository = AlbumRepository(FakeVinilosApiService.successAlbum(expected))

        val result = repository.getAlbum(100)

        assertNotNull(result)
        assertEquals(expected.id, result?.id)
    }

    @Test
    fun `getAlbum devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.httpError(code = 404))

        val result = repository.getAlbum(999)

        assertNull(result)
    }

    @Test
    fun `getAlbum devuelve null cuando la API lanza excepcion`() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.throwing(IllegalStateException("oops")))

        val result = repository.getAlbum(1)

        assertNull(result)
    }
}
