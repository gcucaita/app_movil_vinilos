package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.presentation.uistate.CreateAlbumUiState
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
class CreateAlbumViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        RetrofitInstance.setApiForTesting(defaultFakeApi())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        RetrofitInstance.reset()
    }

    @Test
    fun `createState inicial es Idle`() {
        val vm = CreateAlbumViewModel()
        assertTrue(vm.createState.value is CreateAlbumUiState.Idle)
    }

    @Test
    fun `createAlbum emite ValidationError cuando campos estan vacios`() {
        val vm = CreateAlbumViewModel()

        vm.createAlbum(
            CreateAlbumRequest(
                name = "",
                cover = "",
                releaseDate = "",
                description = "",
                genre = "",
                recordLabel = ""
            ),
            performerId = null
        )

        assertTrue(vm.createState.value is CreateAlbumUiState.ValidationError)
        val errors = (vm.createState.value as CreateAlbumUiState.ValidationError).errors
        assertTrue(errors.name != null)
        assertTrue(errors.cover != null)
        assertTrue(errors.releaseDate != null)
        assertTrue(errors.description != null)
    }

    @Test
    fun `createAlbum emite ValidationError cuando la fecha tiene formato incorrecto`() {
        val vm = CreateAlbumViewModel()

        vm.createAlbum(
            CreateAlbumRequest(
                name = "Buscando America",
                cover = "https://example.com/cover.jpg",
                releaseDate = "01-08-1984",
                description = "Descripcion",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            performerId = null
        )

        assertTrue(vm.createState.value is CreateAlbumUiState.ValidationError)
        val errors = (vm.createState.value as CreateAlbumUiState.ValidationError).errors
        assertTrue(errors.releaseDate != null)
    }

    @Test
    fun `createAlbum emite Success cuando el backend crea el album`() {
        val expected = albumFixture(100)
        RetrofitInstance.setApiForTesting(defaultFakeApi(onCreateAlbum = { Response.success(expected) }))
        val vm = CreateAlbumViewModel()

        vm.createAlbum(
            CreateAlbumRequest(
                name = expected.name,
                cover = expected.cover,
                releaseDate = "1984-08-01",
                description = expected.description.orEmpty(),
                genre = expected.genre.orEmpty(),
                recordLabel = expected.recordLabel.orEmpty()
            ),
            performerId = null
        )
        Thread.sleep(500L)

        val state = vm.createState.value
        assertTrue(state is CreateAlbumUiState.Success)
        assertEquals(expected, (state as CreateAlbumUiState.Success).album)
    }

    @Test
    fun `createAlbum emite Error cuando el backend responde con error HTTP`() {
        RetrofitInstance.setApiForTesting(
            defaultFakeApi(onCreateAlbum = { Response.error(500, okhttp3.ResponseBody.create(okhttp3.MediaType.parse("text/plain"), "boom")) })
        )
        val vm = CreateAlbumViewModel()

        vm.createAlbum(
            CreateAlbumRequest(
                name = "Album",
                cover = "https://example.com/cover.jpg",
                releaseDate = "1984-08-01",
                description = "Descripcion",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            performerId = null
        )
        Thread.sleep(500L)

        assertTrue(vm.createState.value is CreateAlbumUiState.Error)
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

    private fun defaultFakeApi(
        onCreateAlbum: (CreateAlbumRequest) -> Response<Album> = { Response.success(albumFixture(1)) },
    ) = FakeApi(onCreateAlbum = onCreateAlbum)

    private class FakeApi(
        private val onCreateAlbum: (CreateAlbumRequest) -> Response<Album> = { Response.success(
            Album(id = 1, name = "X", cover = "https://x.com/c.jpg", performers = null, tracks = null,
                comments = null, releaseDate = null, description = null, genre = null, recordLabel = null)
        ) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = onCreateAlbum(request)
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())
        override suspend fun getCollector(id: Int): Response<Collector> = error("no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
        override suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> = error("addTrack no aplica")
        override suspend fun getBands(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> = error("addPerformerToAlbum no aplica")
    }
}
