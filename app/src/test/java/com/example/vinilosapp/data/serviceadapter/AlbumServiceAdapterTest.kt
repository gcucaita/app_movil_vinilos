package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
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

    @Test
    fun `createAlbum delega en la API y envia el payload`() = runBlocking {
        val request = CreateAlbumRequest(
            name = "Buscando America",
            cover = "https://example.com/100.jpg",
            releaseDate = "1984-08-01T05:00:00.000Z",
            description = "Descripcion",
            genre = "Salsa",
            recordLabel = "Elektra",
        )
        val expected = albumFixture(id = 100)
        val api = FakeApi(onCreateAlbum = { Response.success(expected) })
        val adapter = AlbumServiceAdapter(api)

        val response = adapter.createAlbum(request)

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(request, api.lastCreateAlbumRequest)
    }

    @Test
    fun `addTrack delega en la API y reenvia albumId y payload`() = runBlocking {
        val request = CreateTrackRequest(name = "So What", duration = "09:22")
        val expected = Track(id = 1, name = "So What", duration = "09:22")
        val api = FakeApi(onAddTrack = { _, _ -> Response.success(expected) })
        val adapter = AlbumServiceAdapter(api)

        val response = adapter.addTrack(albumId = 1, request = request)

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(1, api.lastAddTrackAlbumId)
        assertEquals(request, api.lastAddTrackRequest)
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
        private val onCreateAlbum: (CreateAlbumRequest) -> Response<Album> = { error("createAlbum no stubbeado") },
        private val onAddTrack: (Int, CreateTrackRequest) -> Response<Track> = { _, _ -> error("addTrack no stubbeado") },
    ) : VinilosApiService {

        var getAlbumsCalls: Int = 0
            private set
        var lastRequestedAlbumId: Int? = null
            private set
        var lastCreateAlbumRequest: CreateAlbumRequest? = null
            private set
        var lastAddTrackAlbumId: Int? = null
            private set
        var lastAddTrackRequest: CreateTrackRequest? = null
            private set

        override suspend fun getAlbums(): Response<List<Album>> {
            getAlbumsCalls++
            return onGetAlbums()
        }

        override suspend fun getAlbum(id: Int): Response<Album> {
            lastRequestedAlbumId = id
            return onGetAlbum(id)
        }

        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> {
            lastCreateAlbumRequest = request
            return onCreateAlbum(request)
        }

        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())

        override suspend fun getCollector(id: Int): Response<Collector> = error("getCollector no aplica")

        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())

        override suspend fun getMusician(id: Int): Response<Performer> = error("getMusician no aplica")

        override suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> {
            lastAddTrackAlbumId = albumId
            lastAddTrackRequest = request
            return onAddTrack(albumId, request)
        }

        override suspend fun getBands(): Response<List<Performer>> = Response.success(emptyList())

        override suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> = error("addPerformerToAlbum no aplica")
    }
}
