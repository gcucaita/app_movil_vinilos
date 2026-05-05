package com.example.vinilosapp.data.serviceadapter

import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.CollectorAlbum
import com.example.vinilosapp.domain.model.Performer
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class CollectorServiceAdapterTest {

    @Test
    fun `getCollectors delega en la API y devuelve la respuesta`() = runBlocking {
        val expected = listOf(collectorFixture(id = 100), collectorFixture(id = 101))
        val api = FakeApi(onGetCollectors = { Response.success(expected) })
        val adapter = CollectorServiceAdapter(api)

        val response = adapter.getCollectors()

        assertTrue(response.isSuccessful)
        assertEquals(expected, response.body())
        assertEquals(1, api.getCollectorsCalls)
    }

    private fun collectorFixture(id: Int): Collector = Collector(
        id = id,
        name = "Collector $id",
        telephone = "300000$id",
        email = "collector$id@example.com",
        comments = emptyList(),
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

    private class FakeApi(
        private val onGetCollectors: () -> Response<List<Collector>> = { error("getCollectors no stubbeado") },
    ) : VinilosApiService {

        var getCollectorsCalls: Int = 0
            private set

        override suspend fun getAlbums(): Response<List<Album>> = error("getAlbums no aplica")

        override suspend fun getAlbum(id: Int): Response<Album> = error("getAlbum no aplica")

        override suspend fun getCollectors(): Response<List<Collector>> {
            getCollectorsCalls++
            return onGetCollectors()
        }

        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
    }
}
