package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.repository.MusicianRepository
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.MusicianListUiState
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
class MusicianListViewModelTest {

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
        onGetMusicians: () -> Response<List<Performer>> = { Response.success(emptyList()) }
    ): MusicianListViewModel {
        val repository = MusicianRepository(FakeApi(onGetMusicians = onGetMusicians))
        return MusicianListViewModel(repository)
    }

    @Test
    fun `uiState inicial es Loading`() {
        val vm = viewModel()
        assertTrue(vm.uiState.value is MusicianListUiState.Loading)
    }

    @Test
    fun `loadMusicians emite Success con la lista cuando la API responde 200`() {
        val expected = listOf(musicianFixture(1), musicianFixture(2))
        val vm = viewModel(onGetMusicians = { Response.success(expected) })

        vm.loadMusicians()

        val state = vm.uiState.value
        assertTrue(state is MusicianListUiState.Success)
        assertEquals(expected, (state as MusicianListUiState.Success).musicians)
    }

    @Test
    fun `loadMusicians emite Error cuando la API lanza excepcion`() = runTest(testDispatcher) {
        val vm = viewModel(onGetMusicians = { throw RuntimeException("network error") })

        vm.loadMusicians()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is MusicianListUiState.Error)
    }

    private fun musicianFixture(id: Int) = Performer(
        id = id,
        name = "Musician $id",
        image = "https://example.com/$id.jpg",
        description = "Description $id",
        birthDate = "1948-07-16T05:00:00.000Z"
    )

    private class FakeApi(
        private val onGetMusicians: () -> Response<List<Performer>> = { Response.success(emptyList()) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())

        override suspend fun getCollector(id: Int): Response<Collector> = error("getCollector no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = onGetMusicians()
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
