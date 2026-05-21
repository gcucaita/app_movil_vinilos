package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.AlbumComment
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.CollectorAlbum
import com.example.vinilosapp.domain.model.Performer
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CollectorRepositoryTest {

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
    }

    @After
    fun tearDown() {
        CacheManager.clearAllCaches()
    }

    @Test
    fun `getAllCollectors devuelve lista cuando la API responde 200`() = runBlocking {
        val expected = collectorsFixture()
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollectors = { Response.success(expected) }),
        )

        val result = repository.getAllCollectors()

        assertEquals(expected, result)
    }

    @Test
    fun `getAllCollectors devuelve null cuando la API lanza excepcion`() = runBlocking {
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollectors = { throw RuntimeException("network") }),
        )

        val result = repository.getAllCollectors()

        assertNull(result)
    }

    @Test
    fun `getAllCollectors devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollectors = { Response.error(500, errorBody()) }),
        )

        val result = repository.getAllCollectors()

        assertNull(result)
    }

    @Test
    fun `getAllCollectors sirve desde cache en la segunda llamada`() = runBlocking {
        val expected = collectorsFixture()
        val api = FakeVinilosApiService(onGetCollectors = { Response.success(expected) })
        val repository = CollectorRepository(api)

        val first = repository.getAllCollectors()
        val second = repository.getAllCollectors()

        assertEquals(expected, first)
        assertEquals(expected, second)
        assertEquals(1, api.getCollectorsCalls)
    }

    @Test
    fun `getAllCollectors refresca despues de invalidar cache`() = runBlocking {
        val expected = collectorsFixture()
        val api = FakeVinilosApiService(onGetCollectors = { Response.success(expected) })
        val repository = CollectorRepository(api)

        repository.getAllCollectors()
        CacheManager.invalidateCollectorsListCache()
        repository.getAllCollectors()

        assertEquals(2, api.getCollectorsCalls)
    }

    @Test
    fun `getCollector devuelve collector cuando la API responde 200`() = runBlocking {
        val expected = collectorFixture(id = 100)
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollector = { Response.success(expected) }),
        )

        val result = repository.getCollector(100)

        assertEquals(expected, result)
    }

    @Test
    fun `getCollector devuelve null cuando la API lanza excepcion`() = runBlocking {
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollector = { throw RuntimeException("network") }),
        )

        val result = repository.getCollector(100)

        assertNull(result)
    }

    @Test
    fun `getCollector devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val repository = CollectorRepository(
            FakeVinilosApiService(onGetCollector = { Response.error(500, errorBody()) }),
        )

        val result = repository.getCollector(100)

        assertNull(result)
    }

    private fun collectorsFixture(): List<Collector> = listOf(collectorFixture(id = 100), collectorFixture(id = 101))

    private fun collectorFixture(id: Int): Collector = Collector(
        id = id,
        name = "Collector $id",
        telephone = "300000$id",
        email = "collector$id@example.com",
        comments = listOf(
            AlbumComment(
                id = id,
                description = "Comment $id",
                rating = 5,
            )
        ),
        favoritePerformers = listOf(
            Performer(
                id = id,
                name = "Performer $id",
                image = null,
                description = null,
                birthDate = null,
            )
        ),
        collectorAlbums = listOf(
            CollectorAlbum(
                id = id,
                price = 35,
                status = "Active",
            )
        ),
    )

    private fun errorBody(): ResponseBody =
        ResponseBody.create(MediaType.parse("text/plain"), "server error")

    private class FakeVinilosApiService(
        private val onGetCollectors: () -> Response<List<Collector>> = { error("getCollectors no stubbeado") },
        private val onGetCollector: () -> Response<Collector> = { error("getCollector no stubbeado") },
    ) : VinilosApiService {

        var getCollectorsCalls: Int = 0
            private set

        override suspend fun getAlbums(): Response<List<Album>> = error("getAlbums no aplica")

        override suspend fun getAlbum(id: Int): Response<Album> = error("getAlbum no aplica")

        override suspend fun getCollectors(): Response<List<Collector>> {
            getCollectorsCalls++
            return onGetCollectors()
        }

        override suspend fun getCollector(id: Int): Response<Collector> = onGetCollector()

        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())

        override suspend fun getMusician(id: Int): Response<Performer> = error("getMusician no aplica")
    }
}
