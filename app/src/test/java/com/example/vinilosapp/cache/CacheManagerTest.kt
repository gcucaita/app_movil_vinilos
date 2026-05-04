package com.example.vinilosapp.data.cache

import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.CollectorAlbum
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pruebas unitarias del [CacheManager] — HU1 Consultar catálogo.
 *
 * Verifica:
 *  - getAlbumsList sin datos previos.
 *  - putAlbumsList + getAlbumsList recuperando lista almacenada.
 *  - invalidateAlbumsListCache borrando solo la lista de álbumes.
 *  - clearAllCaches dejando cache vacío.
 *
 * Necesita Robolectric porque android.util.LruCache es de Android.
 */
@RunWith(RobolectricTestRunner::class)
class CacheManagerTest {

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
    }

    @After
    fun tearDown() {
        CacheManager.clearAllCaches()
    }

    @Test
    fun `getAlbumsList devuelve null cuando no hay datos en cache`() {
        assertNull(CacheManager.getAlbumsList())
    }

    @Test
    fun `putAlbumsList y getAlbumsList devuelven la lista almacenada`() {
        val albums = listOf(albumFixture())

        CacheManager.putAlbumsList(albums)

        assertEquals(albums, CacheManager.getAlbumsList())
    }

    @Test
    fun `invalidateAlbumsListCache borra los datos almacenados`() {
        CacheManager.putAlbumsList(listOf(albumFixture()))

        CacheManager.invalidateAlbumsListCache()

        assertNull(CacheManager.getAlbumsList())
    }

    @Test
    fun `getCollectorsList devuelve null cuando no hay datos en cache`() {
        assertNull(CacheManager.getCollectorsList())
    }

    @Test
    fun `putCollectorsList y getCollectorsList devuelven la lista almacenada`() {
        val collectors = listOf(collectorFixture())

        CacheManager.putCollectorsList(collectors)

        assertEquals(collectors, CacheManager.getCollectorsList())
    }

    @Test
    fun `invalidateCollectorsListCache borra los datos almacenados`() {
        CacheManager.putCollectorsList(listOf(collectorFixture()))

        CacheManager.invalidateCollectorsListCache()

        assertNull(CacheManager.getCollectorsList())
    }

    @Test
    fun `clearAllCaches elimina todas las entradas`() {
        CacheManager.putAlbumsList(listOf(albumFixture()))
        CacheManager.putCollectorsList(listOf(collectorFixture()))

        CacheManager.clearAllCaches()

        assertNull(CacheManager.getAlbumsList())
        assertNull(CacheManager.getCollectorsList())
    }

    private fun albumFixture(): Album = Album(
        id = 100,
        name = "Buscando America",
        cover = "https://example.com/cover.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = null,
        genre = "Salsa",
        recordLabel = "Elektra",
    )

    private fun collectorFixture(): Collector = Collector(
        id = 100,
        name = "Manolo Bellon",
        telephone = "3502457896",
        email = "manollo@caracol.com.co",
        comments = emptyList(),
        favoritePerformers = emptyList(),
        collectorAlbums = listOf(
            CollectorAlbum(
                id = 100,
                price = 35,
                status = "Active",
            )
        ),
    )
}
