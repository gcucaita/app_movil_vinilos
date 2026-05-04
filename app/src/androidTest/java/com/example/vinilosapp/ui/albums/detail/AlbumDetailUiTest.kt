package com.example.vinilosapp.ui.albums.detail

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
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.AlbumComment
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.helpers.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AlbumDetailUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(AlbumDetailActivity::class.java, true, false)

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
    fun showsAlbumMetadataAndTracklist() {
        RetrofitInstance.setApiForTesting(
            FakeVinilosApiService(album = albumWithTracks())
        )

        launchActivity(albumId = 1)

        onView(withId(R.id.tvName)).check(matches(withText("Kind of Blue")))
        onView(withId(R.id.tvArtist)).check(matches(withText("Miles Davis")))
        onView(withId(R.id.tvYear)).check(matches(withText("1959")))
        onView(withId(R.id.tvRecordLabel)).check(matches(withText("Columbia")))
        onView(withId(R.id.tvDescription)).check(matches(withText("Modal jazz landmark.")))
        onView(withId(R.id.rvTracks)).check(matches(isDisplayed()))
        onView(withText("So What")).check(matches(isDisplayed()))
        onView(withText("Freddie Freeloader")).check(matches(isDisplayed()))
        onView(withText("09:22")).check(matches(isDisplayed()))
    }

    @Test
    fun hidesTracklistWhenAlbumHasNoTracks() {
        RetrofitInstance.setApiForTesting(
            FakeVinilosApiService(album = albumWithoutTracks())
        )

        launchActivity(albumId = 2)

        onView(withId(R.id.tvName)).check(matches(withText("Blue Train")))
        onView(withId(R.id.rvTracks)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    private fun launchActivity(albumId: Int) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, AlbumDetailActivity::class.java).apply {
            putExtra("albumId", albumId)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private class FakeVinilosApiService(
        private val album: Album,
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> = Response.success(listOf(album))

        override suspend fun getAlbum(id: Int): Response<Album> {
            if (album.id != id) {
                throw IOException("Missing fake detail for album $id")
            }
            return Response.success(album)
        }
    }

    companion object {
        private fun albumWithTracks(): Album = Album(
            id = 1,
            name = "Kind of Blue",
            cover = "https://example.com/kind-of-blue.jpg",
            performers = listOf(
                Performer(
                    id = 10,
                    name = "Miles Davis",
                    image = null,
                    description = null,
                    birthDate = null,
                )
            ),
            tracks = listOf(
                Track(
                    id = 100,
                    name = "So What",
                    duration = "09:22",
                ),
                Track(
                    id = 101,
                    name = "Freddie Freeloader",
                    duration = "09:46",
                ),
            ),
            comments = listOf(
                AlbumComment(
                    id = 2000,
                    description = "Classic.",
                    rating = 5,
                )
            ),
            releaseDate = "1959-08-17T00:00:00.000Z",
            description = "Modal jazz landmark.",
            genre = "Jazz",
            recordLabel = "Columbia",
        )

        private fun albumWithoutTracks(): Album = Album(
            id = 2,
            name = "Blue Train",
            cover = "https://example.com/blue-train.jpg",
            performers = listOf(
                Performer(
                    id = 11,
                    name = "John Coltrane",
                    image = null,
                    description = null,
                    birthDate = null,
                )
            ),
            tracks = emptyList(),
            comments = emptyList(),
            releaseDate = "1957-09-15T00:00:00.000Z",
            description = "Hard bop classic.",
            genre = "Jazz",
            recordLabel = "Blue Note",
        )
    }
}
