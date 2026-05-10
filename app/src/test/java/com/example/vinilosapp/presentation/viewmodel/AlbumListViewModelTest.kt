package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.AlbumListUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        CacheManager.clearAllCaches()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        CacheManager.clearAllCaches()
    }

    private fun viewModel(
        onGetAlbums: () -> Response<List<Album>> = { Response.success(emptyList()) }
    ): AlbumListViewModel {
        val repository = AlbumRepository(
            albumServiceAdapter = AlbumServiceAdapter(FakeApi(onGetAlbums = onGetAlbums)),
            ioDispatcher = testDispatcher
        )
        return AlbumListViewModel(repository)
    }

    @Test
    fun `uiState inicial es Loading`() {
        val vm = viewModel()
        assertTrue(vm.uiState.value is AlbumListUiState.Loading)
    }

    @Test
    fun `loadAlbums emite Success con la lista cuando la API responde 200`() {
        val expected = listOf(albumFixture(1), albumFixture(2))
        val vm = viewModel(onGetAlbums = { Response.success(expected) })

        vm.loadAlbums()

        val state = vm.uiState.value
        assertTrue(state is AlbumListUiState.Success)
        assertEquals(expected, (state as AlbumListUiState.Success).albums)
    }

    @Test
    fun `loadAlbums emite Error cuando la API lanza excepcion`() = runTest(testDispatcher) {
        val vm = viewModel(onGetAlbums = { throw RuntimeException("network error") })

        vm.loadAlbums()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is AlbumListUiState.Error)
    }

    private fun albumFixture(id: Int) = Album(
        id = id,
        name = "Album $id",
        cover = "https://example.com/$id.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = null,
        genre = "Salsa",
        recordLabel = "Elektra"
    )

    private class FakeApi(
        private val onGetAlbums: () -> Response<List<Album>> = { Response.success(emptyList()) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = onGetAlbums()
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
