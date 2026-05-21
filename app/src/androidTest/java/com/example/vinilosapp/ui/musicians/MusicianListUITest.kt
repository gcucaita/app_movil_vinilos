package com.example.vinilosapp.ui.musicians

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.vinilosapp.R
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.helpers.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MusicianListUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(MusicianListActivity::class.java, true, false)

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

    @Test
    fun displaysMusicianListLoadedFromApi() {
        RetrofitInstance.setApiForTesting(FakeApi(musicians = sampleMusicians()))

        launchActivity()

        onView(withId(R.id.musiciansRecyclerView)).check(matches(isDisplayed()))
        onView(withText("Miles Davis")).check(matches(isDisplayed()))
        onView(withText("John Coltrane")).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun showsErrorWhenApiFails() {
        RetrofitInstance.setApiForTesting(
            FakeApi(onGetMusicians = { Response.error(500, okhttp3.ResponseBody.create(null, "")) })
        )

        launchActivity()

        onView(withId(R.id.errorText)).check(matches(isDisplayed()))
    }

    @Test
    fun filtersMusiciansByName() {
        RetrofitInstance.setApiForTesting(FakeApi(musicians = sampleMusicians()))

        launchActivity()

        onView(withId(R.id.searchInput)).perform(replaceText("Miles"))

        onView(withText("Miles Davis")).check(matches(isDisplayed()))
    }

    private fun launchActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MusicianListActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun sampleMusicians() = listOf(
        Performer(id = 1, name = "Miles Davis", image = null, description = "Jazz legend", birthDate = "1926-05-26T00:00:00.000Z"),
        Performer(id = 2, name = "John Coltrane", image = null, description = "Saxophonist", birthDate = "1926-09-23T00:00:00.000Z")
    )

    private class FakeApi(
        private val musicians: List<Performer> = emptyList(),
        private val onGetMusicians: () -> Response<List<Performer>> = { Response.success(musicians) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())

        override suspend fun getCollector(id: Int): Response<Collector> = error("no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = onGetMusicians()
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
