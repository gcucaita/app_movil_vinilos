package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class MusicianServiceAdapterTest {

    @Test
    fun `getMusicians delega en la API y devuelve la respuesta`() = runBlocking {
        val expected = listOf(musicianFixture(id = 100), musicianFixture(id = 101))
        val api = FakeApi(onGetMusicians = { Response.success(expected) })
        val adapter = MusicianServiceAdapter(api)

        val response = adapter.getMusicians()

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(1, api.getMusiciansCalls)
    }

    private fun musicianFixture(id: Int): Performer = Performer(
        id = id,
        name = "Musician $id",
        image = "https://example.com/$id.jpg",
        description = "Description $id",
        birthDate = "1948-07-16T05:00:00.000Z",
    )

    private class FakeApi(
        private val onGetMusicians: () -> Response<List<Performer>> = { error("getMusicians no stubbeado") },
    ) : VinilosApiService {

        var getMusiciansCalls: Int = 0
            private set

        override suspend fun getAlbums(): Response<List<Album>> = error("getAlbums no aplica")

        override suspend fun getAlbum(id: Int): Response<Album> = error("getAlbum no aplica")

        override suspend fun getCollectors(): Response<List<Collector>> = error("getCollectors no aplica")

        override suspend fun getCollector(id: Int): Response<Collector> = error("getCollector no aplica")

        override suspend fun getMusicians(): Response<List<Performer>> {
            getMusiciansCalls++
            return onGetMusicians()
        }
        
        override suspend fun getMusician(id: Int): Response<Performer> = error("getMusician no aplica")
    }
}
