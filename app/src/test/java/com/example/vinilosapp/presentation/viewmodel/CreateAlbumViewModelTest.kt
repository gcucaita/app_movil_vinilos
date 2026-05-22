package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.repository.AlbumRepository
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.CreateAlbumUiState
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
class CreateAlbumViewModelTest {

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

    @Test
    fun `createAlbum emite ValidationError cuando el formulario es invalido`() {
        val vm = viewModel()

        vm.createAlbum("", "bad-url", "fecha", "", "", "")

        assertTrue(vm.uiState.value is CreateAlbumUiState.ValidationError)
    }

    @Test
    fun `createAlbum emite Success cuando el backend crea el album`() = runTest(testDispatcher) {
        val expected = albumFixture(100)
        val vm = viewModel(onCreateAlbum = { Response.success(expected) })

        vm.createAlbum(
            name = expected.name,
            cover = expected.cover,
            releaseDate = expected.releaseDate.orEmpty(),
            description = expected.description.orEmpty(),
            genre = expected.genre.orEmpty(),
            recordLabel = expected.recordLabel.orEmpty(),
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertTrue(state is CreateAlbumUiState.Success)
        assertEquals(expected, (state as CreateAlbumUiState.Success).album)
    }

    @Test
    fun `createAlbum emite Error cuando el backend falla`() = runTest(testDispatcher) {
        val vm = viewModel(onCreateAlbum = { Response.error(500, okhttp3.ResponseBody.create(okhttp3.MediaType.parse("text/plain"), "boom")) })

        vm.createAlbum(
            name = "Album",
            cover = "https://example.com/cover.jpg",
            releaseDate = "1984-08-01T05:00:00.000Z",
            description = "Descripcion",
            genre = "Salsa",
            recordLabel = "Elektra",
        )
        advanceUntilIdle()

        assertTrue(vm.uiState.value is CreateAlbumUiState.Error)
    }

    private fun viewModel(
        onCreateAlbum: (CreateAlbumRequest) -> Response<Album> = { Response.success(albumFixture(1)) },
    ): CreateAlbumViewModel {
        val repository = AlbumRepository(
            albumServiceAdapter = AlbumServiceAdapter(FakeApi(onCreateAlbum = onCreateAlbum)),
            ioDispatcher = testDispatcher,
        )
        return CreateAlbumViewModel(repository)
    }

    private fun albumFixture(id: Int) = Album(
        id = id,
        name = "Album $id",
        cover = "https://example.com/$id.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "Descripcion $id",
        genre = "Salsa",
        recordLabel = "Elektra",
    )

    private class FakeApi(
        private val onCreateAlbum: (CreateAlbumRequest) -> Response<Album>,
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = onCreateAlbum(request)
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())
        override suspend fun getCollector(id: Int): Response<Collector> = error("no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
