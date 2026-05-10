package com.example.vinilosapp.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.repository.CollectorRepository
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.presentation.uistate.CollectorListUiState
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
class CollectorListViewModelTest {

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
        onGetCollectors: () -> Response<List<Collector>> = { Response.success(emptyList()) }
    ): CollectorListViewModel {
        val repository = CollectorRepository(FakeApi(onGetCollectors = onGetCollectors))
        return CollectorListViewModel(repository)
    }

    @Test
    fun `uiState inicial es Loading`() {
        val vm = viewModel()
        assertTrue(vm.uiState.value is CollectorListUiState.Loading)
    }

    @Test
    fun `loadCollectors emite Success con la lista cuando la API responde 200`() {
        val expected = listOf(collectorFixture(1), collectorFixture(2))
        val vm = viewModel(onGetCollectors = { Response.success(expected) })

        vm.loadCollectors()

        val state = vm.uiState.value
        assertTrue(state is CollectorListUiState.Success)
        assertEquals(expected, (state as CollectorListUiState.Success).collectors)
    }

    @Test
    fun `loadCollectors emite Error cuando la API lanza excepcion`() = runTest(testDispatcher) {
        val vm = viewModel(onGetCollectors = { throw RuntimeException("network error") })

        vm.loadCollectors()
        advanceUntilIdle()

        assertTrue(vm.uiState.value is CollectorListUiState.Error)
    }

    private fun collectorFixture(id: Int) = Collector(
        id = id,
        name = "Collector $id",
        telephone = "300000$id",
        email = "collector$id@example.com",
        comments = emptyList(),
        favoritePerformers = emptyList(),
        collectorAlbums = emptyList()
    )

    private class FakeApi(
        private val onGetCollectors: () -> Response<List<Collector>> = { Response.success(emptyList()) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = onGetCollectors()
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}