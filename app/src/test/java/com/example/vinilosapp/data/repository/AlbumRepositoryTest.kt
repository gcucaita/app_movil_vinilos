package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

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

    @Test
    fun `createAlbum devuelve album e invalida cache cuando la API responde 200`() = runBlocking {
        val request = createAlbumRequest()
        val expected = albumFixture(id = 999)
        val api = FakeVinilosApiService(onCreateAlbum = { Response.success(expected) })
        val repository = AlbumRepository(api)

        CacheManager.putAlbumsList(catalogo())
        val result = repository.createAlbum(request)

        assertNotNull(result)
        assertEquals(expected, result)
        assertEquals(request, api.lastCreateAlbumRequest)
        assertNull(CacheManager.getAlbumsList())
    }

    @Test
    fun `createAlbum devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val api = FakeVinilosApiService(onCreateAlbum = { Response.error(400, errorBody()) })
        val repository = AlbumRepository(api)

        val result = repository.createAlbum(createAlbumRequest())

        assertNull(result)
    }

    @Test
    fun `createAlbum propaga excepcion cuando la API lanza excepcion`() = runBlocking {
        val api = FakeVinilosApiService(onCreateAlbum = { throw IllegalStateException("oops") })
        val repository = AlbumRepository(api)

        var caught: Throwable? = null
        try {
            repository.createAlbum(createAlbumRequest())
        } catch (e: IllegalStateException) {
            caught = e
        }

        assertNotNull(caught)
    }

    // HU08 – Asociar tracks con álbum

    @Test
    fun `addTrack devuelve track cuando la API responde 200`() = runBlocking {
        val request = CreateTrackRequest(name = "So What", duration = "09:22")
        val expected = Track(id = 1, name = "So What", duration = "09:22")
        val api = FakeVinilosApiService(onAddTrack = { _, _ -> Response.success(expected) })
        val repository = AlbumRepository(api)

        val result = repository.addTrack(albumId = 1, request = request)

        assertEquals(expected, result)
    }

    @Test
    fun `addTrack devuelve null cuando la API responde con body nulo`() = runBlocking {
        val api = FakeVinilosApiService(onAddTrack = { _, _ -> Response.success(null) })
        val repository = AlbumRepository(api)

        val result = repository.addTrack(albumId = 1, CreateTrackRequest("X", "00:30"))

        assertNull(result)
    }

    @Test
    fun `addTrack propaga excepcion cuando la API lanza excepcion`() = runBlocking {
        val api = FakeVinilosApiService(onAddTrack = { _, _ -> throw IllegalStateException("timeout") })
        val repository = AlbumRepository(api)

        var caught: Throwable? = null
        try {
            repository.addTrack(albumId = 1, CreateTrackRequest("X", "00:30"))
        } catch (e: IllegalStateException) {
            caught = e
        }

        assertNotNull(caught)
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

    private fun createAlbumRequest() = CreateAlbumRequest(
        name = "Buscando America",
        cover = "https://example.com/cover.jpg",
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "Descripcion",
        genre = "Salsa",
        recordLabel = "Elektra",
    )

private fun errorBody(): ResponseBody =
    ResponseBody.create(MediaType.parse("text/plain"), "server error")
    
    private class FakeVinilosApiService(
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

        override suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> = onAddTrack(albumId, request)

        override suspend fun getBands(): Response<List<Performer>> = Response.success(emptyList())

        override suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> = error("addPerformerToAlbum no aplica")
    }
}
