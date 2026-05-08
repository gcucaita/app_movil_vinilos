package com.example.vinilosapp.ui.collectors

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
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
class CollectorListUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(CollectorListActivity::class.java, true, false)

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
    fun displaysCollectorListLoadedFromApi() {
        RetrofitInstance.setApiForTesting(FakeApi(collectors = sampleCollectors()))

        launchActivity()

        onView(withId(R.id.collectorsRecyclerView)).check(matches(isDisplayed()))
        onView(withText("Manolo Bellon")).check(matches(isDisplayed()))
        onView(withText("Juan Gomez")).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun showsErrorWhenApiFails() {
        RetrofitInstance.setApiForTesting(
            FakeApi(onGetCollectors = { throw IOException("network error") })
        )

        launchActivity()

        onView(withId(R.id.errorText)).check(matches(isDisplayed()))
    }

    @Test
    fun filtersCollectorsByName() {
        RetrofitInstance.setApiForTesting(FakeApi(collectors = sampleCollectors()))

        launchActivity()

        onView(withId(R.id.searchInput)).perform(replaceText("Manolo"))

        onView(withText("Manolo Bellon")).check(matches(isDisplayed()))
    }

    private fun launchActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, CollectorListActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun sampleCollectors() = listOf(
        Collector(id = 1, name = "Manolo Bellon", telephone = "3502457896", email = "manollo@caracol.com.co", comments = emptyList(), favoritePerformers = emptyList(), collectorAlbums = emptyList()),
        Collector(id = 2, name = "Juan Gomez", telephone = "3107654321", email = "juan@example.com", comments = emptyList(), favoritePerformers = emptyList(), collectorAlbums = emptyList())
    )

    private class FakeApi(
        private val collectors: List<Collector> = emptyList(),
        private val onGetCollectors: () -> Response<List<Collector>> = { Response.success(collectors) },
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = onGetCollectors()
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = error("no aplica")
    }
}
