package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.repository.MusicianRepository
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.MusicianDetailUiState
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
class MusicianDetailViewModelTest {

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
        onGetMusician: (Int) -> Response<Performer> = { error("no stubbeado") }
    ): MusicianDetailViewModel {
        val repository = MusicianRepository(FakeApi(onGetMusician = onGetMusician))
        return MusicianDetailViewModel(repository)
    }

    @Test
    fun `uiState inicial es Loading`() {
        val vm = viewModel()
        assertTrue(vm.uiState.value is MusicianDetailUiState.Loading)
    }

    @Test
    fun `loadMusician emite Success con el artista cuando la API responde 200`() {
        val expected = musicianFixture(7)
        val vm = viewModel(onGetMusician = { Response.success(expected) })

        vm.loadMusician(7)

        val state = vm.uiState.value
        assertTrue(state is MusicianDetailUiState.Success)
        assertEquals(expected, (state as MusicianDetailUiState.Success).musician)
    }

    @Test
    fun `loadMusician emite Error cuando la API lanza excepcion`() {
        val vm = viewModel(onGetMusician = { throw RuntimeException("network error") })

        vm.loadMusician(1)

        assertTrue(vm.uiState.value is MusicianDetailUiState.Error)
    }

    @Test
    fun `loadMusician emite Error cuando la API responde null`() {
        val vm = viewModel(onGetMusician = { Response.success(null) })

        vm.loadMusician(1)

        assertTrue(vm.uiState.value is MusicianDetailUiState.Error)
    }

    private fun musicianFixture(id: Int) = Performer(
        id = id,
        name = "Musician $id",
        image = "https://example.com/$id.jpg",
        description = "Description $id",
        birthDate = "1948-07-16T05:00:00.000Z"
    )

    private class FakeApi(
        private val onGetMusician: (Int) -> Response<Performer> = { error("no stubbeado") },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())

        override suspend fun getCollector(id: Int): Response<Collector> = error("getCollector no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = onGetMusician(id)
    }
}
