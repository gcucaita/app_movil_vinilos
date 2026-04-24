package com.example.vinilosapp.data.repository

import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.AlbumComment
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.runner.RunWith
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class AlbumRepositoryTest {

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
    }

    @Test
    fun getAllAlbums_returnsAlbums_whenApiResponseIsSuccessful() = runBlocking {
        val expectedAlbums = listOf(
            Album(
                id = 100,
                name = "Buscando America",
                cover = "https://example.com/cover.jpg",
                performers = listOf(
                    Performer(
                        id = 1,
                        name = "Ruben Blades",
                        image = null,
                        description = null,
                        birthDate = null
                    )
                ),
                tracks = listOf(
                    Track(
                        id = 1,
                        name = "Decisiones",
                        duration = "5:05"
                    )
                ),
                comments = listOf(
                    AlbumComment(
                        id = 1,
                        description = "Excelente",
                        rating = 5
                    )
                ),
                releaseDate = "1984-08-01T05:00:00.000Z",
                description = "Album de prueba",
                genre = "Salsa",
                recordLabel = "Elektra"
            )
        )
        val repository = AlbumRepository(FakeVinilosApiService.success(expectedAlbums))

        val result = repository.getAllAlbums()

        assertEquals(expectedAlbums, result)
    }

    @Test
    fun getAllAlbums_returnsNull_whenApiThrowsException() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.exception(RuntimeException("network error")))

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun getAllAlbums_returnsNull_whenApiResponseIsNotSuccessful() = runBlocking {
        val repository = AlbumRepository(FakeVinilosApiService.error())

        val result = repository.getAllAlbums()

        assertNull(result)
    }

    @Test
    fun getAllAlbums_returnsCachedAlbums_whenAvailable() = runBlocking {
        val expectedAlbums = listOf(
            Album(
                id = 100,
                name = "Buscando America",
                cover = "https://example.com/cover.jpg",
                performers = emptyList(),
                tracks = emptyList(),
                comments = emptyList(),
                releaseDate = "1984-08-01T05:00:00.000Z",
                description = "Album de prueba",
                genre = "Salsa",
                recordLabel = "Elektra"
            )
        )
        val apiService = FakeVinilosApiService.success(expectedAlbums)
        val repository = AlbumRepository(apiService)

        val firstResult = repository.getAllAlbums()
        val secondResult = repository.getAllAlbums()

        assertEquals(expectedAlbums, firstResult)
        assertEquals(expectedAlbums, secondResult)
        assertEquals(1, apiService.callCount)
    }
}

private class FakeVinilosApiService(
    private val responseProvider: suspend () -> Response<List<Album>>
) : VinilosApiService {

    var callCount: Int = 0
        private set

    override suspend fun getAlbums(): Response<List<Album>> {
        callCount++
        return responseProvider()
    }

    companion object {
        fun success(albums: List<Album>) = FakeVinilosApiService {
            Response.success(albums)
        }

        fun error() = FakeVinilosApiService {
            Response.error(500, ResponseBody.create(null, "server error"))
        }

        fun exception(throwable: Throwable) = FakeVinilosApiService {
            throw throwable
        }
    }
}
