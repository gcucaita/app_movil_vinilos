package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class MusicianRepositoryTest {

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
    }

    @After
    fun tearDown() {
        CacheManager.clearAllCaches()
    }

    @Test
    fun `getAllMusicians devuelve lista cuando la API responde 200`() = runBlocking {
        val expected = musiciansFixture()
        val repository = MusicianRepository(
            FakeVinilosApiService(onGetMusicians = { Response.success(expected) }),
        )

        val result = repository.getAllMusicians()

        assertEquals(expected, result)
    }

    @Test
    fun `getAllMusicians devuelve null cuando la API lanza excepcion`() = runBlocking {
        val repository = MusicianRepository(
            FakeVinilosApiService(onGetMusicians = { throw RuntimeException("network") }),
        )

        val result = repository.getAllMusicians()

        assertNull(result)
    }

    @Test
    fun `getAllMusicians devuelve null cuando la API responde con error HTTP`() = runBlocking {
        val repository = MusicianRepository(
            FakeVinilosApiService(onGetMusicians = { Response.error(500, errorBody()) }),
        )

        val result = repository.getAllMusicians()

        assertNull(result)
    }

    @Test
    fun `getAllMusicians sirve desde cache en la segunda llamada`() = runBlocking {
        val expected = musiciansFixture()
        val api = FakeVinilosApiService(onGetMusicians = { Response.success(expected) })
        val repository = MusicianRepository(api)

        val first = repository.getAllMusicians()
        val second = repository.getAllMusicians()

        assertEquals(expected, first)
        assertEquals(expected, second)
        assertEquals(1, api.getMusiciansCalls)
    }

    @Test
    fun `getAllMusicians refresca despues de invalidar cache`() = runBlocking {
        val expected = musiciansFixture()
        val api = FakeVinilosApiService(onGetMusicians = { Response.success(expected) })
        val repository = MusicianRepository(api)

        repository.getAllMusicians()
        CacheManager.invalidateMusiciansListCache()
        repository.getAllMusicians()

        assertEquals(2, api.getMusiciansCalls)
    }

    private fun musiciansFixture(): List<Performer> = listOf(musicianFixture(id = 100), musicianFixture(id = 101))

    private fun musicianFixture(id: Int): Performer = Performer(
        id = id,
        name = "Musician $id",
        image = "https://example.com/$id.jpg",
        description = "Description $id",
        birthDate = "1948-07-16T05:00:00.000Z",
    )

    private fun errorBody(): ResponseBody =
        ResponseBody.create(MediaType.parse("text/plain"), "server error")

    private class FakeVinilosApiService(
        private val onGetMusicians: () -> Response<List<Performer>> = { error("getMusicians no stubbeado") },
    ) : VinilosApiService {

        var getMusiciansCalls: Int = 0
            private set

        override suspend fun getAlbums(): Response<List<Album>> = error("getAlbums no aplica")

        override suspend fun getAlbum(id: Int): Response<Album> = error("getAlbum no aplica")

        override suspend fun getCollectors(): Response<List<Collector>> = error("getCollectors no aplica")

        override suspend fun getMusicians(): Response<List<Performer>> {
            getMusiciansCalls++
            return onGetMusicians()
        }
        override suspend fun getMusician(id: Int): Response<Performer> = error("getMusician no aplica")
    }
}
