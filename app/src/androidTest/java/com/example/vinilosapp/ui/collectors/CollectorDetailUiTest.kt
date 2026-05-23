package com.example.vinilosapp.ui.collectors

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.vinilosapp.R
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.CollectorAlbum
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.helpers.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class CollectorDetailUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(CollectorDetailActivity::class.java, true, false)

    @Before
    fun setUp() {
        CacheManager.clearAllCaches()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        idlingRegistered = true
    }

    @After
    fun tearDown() {
        if (idlingRegistered) {
            IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
            idlingRegistered = false
        }
        CacheManager.clearAllCaches()
        RetrofitInstance.reset()
    }

    // HU06 – Consultar la información detallada de coleccionista

    @Test
    fun muestraNombreEmailYTelefonoDelColeccionista() {
        RetrofitInstance.setApiForTesting(FakeApi(collector = collectorConAlbumes()))

        launchActivity(collectorId = 1)

        onView(withId(R.id.tvName)).check(matches(withText("Manolo Bellon")))
        onView(withId(R.id.tvEmail)).check(matches(withText("manollo@caracol.com.co")))
        onView(withId(R.id.tvPhone)).check(matches(withText("3502457896")))
    }

    @Test
    fun muestraListaDeAlbumesDelColeccionistaCuandoTieneAlbumes() {
        RetrofitInstance.setApiForTesting(FakeApi(collector = collectorConAlbumes()))

        launchActivity(collectorId = 1)

        onView(withId(R.id.rvAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun ocultaListaDeAlbumesWhenColeccionistaNoTieneAlbumes() {
        RetrofitInstance.setApiForTesting(FakeApi(collector = collectorSinAlbumes()))

        launchActivity(collectorId = 2)

        onView(withId(R.id.rvAlbums)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun muestraScrollViewCuandoLosDatosCargaCorrectamente() {
        RetrofitInstance.setApiForTesting(FakeApi(collector = collectorConAlbumes()))

        launchActivity(collectorId = 1)

        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
    }

    private fun launchActivity(collectorId: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, CollectorDetailActivity::class.java).apply {
            putExtra("collectorId", collectorId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun collectorConAlbumes() = Collector(
        id = 1,
        name = "Manolo Bellon",
        telephone = "3502457896",
        email = "manollo@caracol.com.co",
        comments = emptyList(),
        favoritePerformers = emptyList(),
        collectorAlbums = listOf(
            CollectorAlbum(id = 10, price = 35, status = "Active")
        )
    )

    private fun collectorSinAlbumes() = Collector(
        id = 2,
        name = "Juan Gomez",
        telephone = "3107654321",
        email = "juan@example.com",
        comments = emptyList(),
        favoritePerformers = emptyList(),
        collectorAlbums = emptyList()
    )

    private class FakeApi(
        private val collector: Collector,
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(listOf(collector))
        override suspend fun getCollector(id: Int): Response<Collector> = Response.success(collector)
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
        override suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> = error("addTrack no aplica")
        override suspend fun getBands(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> = error("addPerformerToAlbum no aplica")
    }
}
