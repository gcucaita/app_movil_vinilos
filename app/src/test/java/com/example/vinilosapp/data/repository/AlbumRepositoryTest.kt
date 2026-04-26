package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

/**
 * Pruebas unitarias del repo de álbumes — HU1 Consultar catálogo.
 *
 * Cubre success, error , exception y cache hit
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
        val expected = catalogo()
        val repository = AlbumRepository(
            FakeVinilosApiService(onGetAlbums = { Response.success(expected) }),
        )

        val result = repository.getAllAlbums()

        assertEquals(expected, result)
    }

    @Test
    fun `getAllAlbums devuelve null cuando la API lanza excepcion`() = runBlocking {
        val api = FakeVinilosApiService(onGetAlbums = { throw RuntimeException("network") })
        val repository = AlbumRepository(api)

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun `getAllAlbums devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val api = FakeVinilosApiService(onGetAlbums = { Response.error(500, errorBody()) })
        val repository = AlbumRepository(api)

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun `getAllAlbums sirve desde cache en la segunda llamada`() = runBlocking {
        val expected = catalogo()
        val api = FakeVinilosApiService(onGetAlbums = { Response.success(expected) })
        val repository = AlbumRepository(api)

        val first = repository.getAllAlbums()
        val second = repository.getAllAlbums()

        assertEquals(expected, first)
        assertEquals(expected, second)
        assertEquals(1, api.getAlbumsCalls)
    }

    @Test
    fun `getAllAlbums refresca despues de invalidar cache`() = runBlocking {
        val expected = catalogo()
        val api = FakeVinilosApiService(onGetAlbums = { Response.success(expected) })
        val repository = AlbumRepository(api)

        repository.getAllAlbums()
        CacheManager.invalidateAlbumsListCache()
        repository.getAllAlbums()

        assertEquals(2, api.getAlbumsCalls)
    }

    @Test
    fun `getAlbum devuelve album cuando la API responde 200`() = runBlocking {
        val expected = albumFixture(id = 100)
        val api = FakeVinilosApiService(onGetAlbum = { Response.success(expected) })
        val repository = AlbumRepository(api)

        val result = repository.getAlbum(100)

        assertNotNull(result)
        assertEquals(expected.id, result?.id)
        assertEquals(100, api.lastRequestedAlbumId)
    }

    @Test
    fun `getAlbum devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val api = FakeVinilosApiService(onGetAlbum = { Response.error(404, errorBody()) })
        val repository = AlbumRepository(api)

        val result = repository.getAlbum(999)

        assertNull(result)
    }

    @Test
    fun `getAlbum devuelve null cuando la API lanza excepcion`() = runBlocking {
        val api = FakeVinilosApiService(onGetAlbum = { throw IllegalStateException("oops") })
        val repository = AlbumRepository(api)

        val result = repository.getAlbum(1)

        assertNull(result)
    }

    private fun catalogo(): List<Album> = listOf(albumFixture(id = 100), albumFixture(id = 101))

    private fun albumFixture(id: Int): Album = Album(
        id = id,
        name = "Album $id",
        cover = "https://example.com/$id.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = null,
        genre = "Salsa",
        recordLabel = "Elektra",
    )

    private fun errorBody() = "server error".toResponseBody("text/plain".toMediaTypeOrNull())

    private class FakeVinilosApiService(
        private val onGetAlbums: () -> Response<List<Album>> = { error("getAlbums no stubbeado") },
        private val onGetAlbum: (Int) -> Response<Album> = { error("getAlbum no stubbeado") },
    ) : VinilosApiService {

        var getAlbumsCalls: Int = 0
            private set
        var lastRequestedAlbumId: Int? = null
            private set

        override suspend fun getAlbums(): Response<List<Album>> {
            getAlbumsCalls++
            return onGetAlbums()
        }

        override suspend fun getAlbum(id: Int): Response<Album> {
            lastRequestedAlbumId = id
            return onGetAlbum(id)
        }
    }
}