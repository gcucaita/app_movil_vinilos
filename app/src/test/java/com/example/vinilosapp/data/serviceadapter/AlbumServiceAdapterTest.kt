package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

/**
 * Pruebas unitarias del adapter — HU1 Consultar catálogo.
 */
class AlbumServiceAdapterTest {

    @Test
    fun `getAlbums delega en la API y devuelve la respuesta`() = runBlocking {
        val expected = listOf(albumFixture(id = 100), albumFixture(id = 101))
        val api = FakeApi(onGetAlbums = { Response.success(expected) })
        val adapter = AlbumServiceAdapter(api)

        val response = adapter.getAlbums()

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(1, api.getAlbumsCalls)
    }

    @Test
    fun `getAlbum delega en la API y reenvia el id`() = runBlocking {
        val expected = albumFixture(id = 42)
        val api = FakeApi(onGetAlbum = { Response.success(expected) })
        val adapter = AlbumServiceAdapter(api)

        val response = adapter.getAlbum(42)

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(42, api.lastRequestedAlbumId)
    }

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

    private class FakeApi(
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