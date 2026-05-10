package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.AlbumDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class AlbumDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        onGetAlbum: (Int) -> Response<Album> = { error("no stubbeado") }
    ): AlbumDetailViewModel {
        val repository = AlbumRepository(
            albumServiceAdapter = AlbumServiceAdapter(FakeApi(onGetAlbum = onGetAlbum)),
            ioDispatcher = testDispatcher
        )
        return AlbumDetailViewModel(repository)
    }

    @Test
    fun `uiState inicial es Loading`() {
        val vm = viewModel()
        assertTrue(vm.uiState.value is AlbumDetailUiState.Loading)
    }

    @Test
    fun `loadAlbum emite Success con el album cuando la API responde 200`() {
        val expected = albumFixture(42)
        val vm = viewModel(onGetAlbum = { Response.success(expected) })

        vm.loadAlbum(42)

        val state = vm.uiState.value
        assertTrue(state is AlbumDetailUiState.Success)
        assertEquals(expected, (state as AlbumDetailUiState.Success).album)
    }

    @Test
    fun `loadAlbum emite Error cuando la API lanza excepcion`() {
        val vm = viewModel(onGetAlbum = { throw RuntimeException("network error") })

        vm.loadAlbum(1)

        assertTrue(vm.uiState.value is AlbumDetailUiState.Error)
    }

    @Test
    fun `loadAlbum emite Error cuando la API responde null`() {
        val vm = viewModel(onGetAlbum = { Response.success(null) })

        vm.loadAlbum(1)

        assertTrue(vm.uiState.value is AlbumDetailUiState.Error)
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
        private val onGetAlbum: (Int) -> Response<Album> = { error("no stubbeado") },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = onGetAlbum(id)
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
