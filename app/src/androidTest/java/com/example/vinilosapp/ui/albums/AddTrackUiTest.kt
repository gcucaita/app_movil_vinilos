package com.example.vinilosapp.ui.albums

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
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
class AddTrackUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(AddTrackActivity::class.java, true, false)

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

    // HU08 – Asociar tracks con un álbum

    @Test
    fun muestraFormularioDeTracks() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity(albumId = 1)

        onView(withId(R.id.etTrackName)).check(matches(isDisplayed()))
        onView(withId(R.id.etTrackDuration)).check(matches(isDisplayed()))
        onView(withId(R.id.btnAddTrack)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTrackCount)).check(matches(isDisplayed()))
    }

    @Test
    fun muestraCeroTracksCuandoSeAbreLaActividad() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity(albumId = 1)

        onView(withId(R.id.tvTrackCount)).check(matches(withText("0 TRACKS ADDED")))
    }

    @Test
    fun agregarTrackActualizaContadorYMuestraElTrack() {
        val trackCreado = Track(id = 1, name = "So What", duration = "09:22")
        RetrofitInstance.setApiForTesting(FakeApi(onAddTrack = { _, _ -> Response.success(trackCreado) }))

        launchActivity(albumId = 1)

        onView(withId(R.id.etTrackName)).perform(replaceText("So What"))
        onView(withId(R.id.etTrackDuration)).perform(replaceText("09:22"))
        onView(withId(R.id.btnAddTrack)).perform(click())

        onView(withId(R.id.tvTrackCount)).check(matches(withText("1 TRACKS ADDED")))
        onView(withText("So What")).check(matches(isDisplayed()))
    }

    @Test
    fun agregarVariosTracksActualizaContadorCorrectamente() {
        var trackIdCounter = 0
        RetrofitInstance.setApiForTesting(
            FakeApi(onAddTrack = { _, req ->
                Response.success(Track(id = ++trackIdCounter, name = req.name, duration = req.duration))
            })
        )

        launchActivity(albumId = 1)

        onView(withId(R.id.etTrackName)).perform(replaceText("Freddie Freeloader"))
        onView(withId(R.id.etTrackDuration)).perform(replaceText("09:46"))
        onView(withId(R.id.btnAddTrack)).perform(click())

        onView(withId(R.id.etTrackName)).perform(replaceText("Blue in Green"))
        onView(withId(R.id.etTrackDuration)).perform(replaceText("05:37"))
        onView(withId(R.id.btnAddTrack)).perform(click())

        onView(withId(R.id.tvTrackCount)).check(matches(withText("2 TRACKS ADDED")))
    }

    @Test
    fun botonAgregarEstaHabilitadoAlIniciar() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity(albumId = 1)

        onView(withId(R.id.btnAddTrack)).check(matches(isEnabled()))
    }

    private fun launchActivity(albumId: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AddTrackActivity::class.java).apply {
            putExtra("albumId", albumId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private class FakeApi(
        private val onAddTrack: (Int, CreateTrackRequest) -> Response<Track> = { _, req ->
            Response.success(Track(id = 1, name = req.name, duration = req.duration))
        },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())
        override suspend fun getCollector(id: Int): Response<Collector> = error("no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
        override suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Response<Track> = onAddTrack(albumId, request)
        override suspend fun getBands(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Response<Album> = error("addPerformerToAlbum no aplica")
    }
}
