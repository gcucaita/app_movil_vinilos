package com.example.vinilosapp.ui.musicians

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.vinilosapp.R
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
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

@RunWith(AndroidJUnit4::class)
class MusicianDetailUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(MusicianDetailActivity::class.java, true, false)

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
    fun showsMusicianNameAndDescription() {
        RetrofitInstance.setApiForTesting(FakeApi(musician = sampleMusician()))

        launchActivity(musicianId = 1)

        onView(withId(R.id.tvName)).check(matches(withText("Miles Davis")))
        onView(withId(R.id.tvDescription)).check(matches(withText("Trompetista de jazz")))
    }

    @Test
    fun showsScrollViewWhenDataLoads() {
        RetrofitInstance.setApiForTesting(FakeApi(musician = sampleMusician()))

        launchActivity(musicianId = 1)

        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
    }

    private fun launchActivity(musicianId: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MusicianDetailActivity::class.java).apply {
            putExtra("musicianId", musicianId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun sampleMusician() = Performer(
        id = 1,
        name = "Miles Davis",
        image = null,
        description = "Trompetista de jazz",
        birthDate = "1926-05-26T00:00:00.000Z"
    )

    private class FakeApi(
        private val musician: Performer,
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(emptyList())
        override suspend fun getAlbum(id: Int): Response<Album> = error("no aplica")
        override suspend fun createAlbum(request: CreateAlbumRequest): Response<Album> = error("no aplica")
        override suspend fun getCollectors(): Response<List<Collector>> = Response.success(emptyList())

        override suspend fun getCollector(id: Int): Response<Collector> = error("no aplica")
        override suspend fun getMusicians(): Response<List<Performer>> = Response.success(emptyList())
        override suspend fun getMusician(id: Int): Response<Performer> = Response.success(musician)
    }
}
