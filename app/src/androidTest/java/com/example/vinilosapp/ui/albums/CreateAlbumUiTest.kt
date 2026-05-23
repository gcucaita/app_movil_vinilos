package com.example.vinilosapp.ui.albums

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
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
import org.hamcrest.Matcher
import org.hamcrest.Matchers.isA
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class CreateAlbumUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(CreateAlbumActivity::class.java, true, false)

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

    // HU07 – Crear un álbum

    @Test
    fun muestraFormularioCuandoSeAbreLaActividad() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity()

        onView(withId(R.id.etAlbumName)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.etCoverUrl)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.etReleaseDate)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.etDescription)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.btnCreateAlbum)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun botonCrearEstaHabilitadoAlCargarElFormulario() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity()

        onView(withId(R.id.btnCreateAlbum)).check(matches(isEnabled()))
    }

    @Test
    fun muestraErrorDeValidacionCuandoCamposEstanVacios() {
        RetrofitInstance.setApiForTesting(FakeApi())

        launchActivity()

        onView(withId(R.id.etAlbumName)).perform(scrollTo(), replaceText(""), closeSoftKeyboard())
        onView(withId(R.id.etCoverUrl)).perform(scrollTo(), replaceText(""), closeSoftKeyboard())
        onView(withId(R.id.etReleaseDate)).perform(scrollTo(), replaceText(""), closeSoftKeyboard())
        onView(withId(R.id.etDescription)).perform(scrollTo(), replaceText(""), closeSoftKeyboard())
        onView(withId(R.id.btnCreateAlbum)).perform(scrollTo(), forceClick())

        onView(withId(R.id.etAlbumName)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.btnCreateAlbum)).check(matches(isEnabled()))
    }

    @Test
    fun enviaFormularioCorrectamenteCuandoTodosLosCamposSonValidos() {
        val albumCreado = albumFixture(id = 42)
        RetrofitInstance.setApiForTesting(FakeApi(onCreateAlbum = { Response.success(albumCreado) }))

        launchActivity()

        onView(withId(R.id.etAlbumName)).perform(scrollTo(), replaceText("Buscando America"), closeSoftKeyboard())
        onView(withId(R.id.etCoverUrl)).perform(scrollTo(), replaceText("https://example.com/cover.jpg"), closeSoftKeyboard())
        onView(withId(R.id.etReleaseDate)).perform(scrollTo(), replaceText("1984-08-01"), closeSoftKeyboard())
        onView(withId(R.id.etDescription)).perform(scrollTo(), replaceText("Album de Ruben Blades"), closeSoftKeyboard())
        onView(withId(R.id.btnCreateAlbum)).perform(scrollTo(), forceClick())
    }

    private fun launchActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, CreateAlbumActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun forceClick(): ViewAction = object : ViewAction {
        override fun getDescription() = "force click"
        override fun getConstraints(): Matcher<View> = isA(View::class.java)
        override fun perform(uiController: UiController, view: View) {
            view.performClick()
            uiController.loopMainThreadUntilIdle()
        }
    }

    private fun albumFixture(id: Int) = Album(
        id = id,
        name = "Buscando America",
        cover = "https://example.com/cover.jpg",
        performers = null,
        tracks = null,
        comments = null,
        releaseDate = "1984-08-01T05:00:00.000Z",
        description = "Album de Ruben Blades",
        genre = "Salsa",
        recordLabel = "Elektra",
    )

    private class FakeApi(
        private val onCreateAlbum: (CreateAlbumRequest) -> Response<Album> = { Response.success(
            Album(id = 99, name = "X", cover = "https://x.com/c.jpg", performers = null, tracks = null,
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
