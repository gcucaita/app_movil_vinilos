package com.example.vinilosapp.ui.albums.list

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.vinilosapp.MainActivity
import com.example.vinilosapp.R
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.AlbumComment
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.helpers.EspressoIdlingResource
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.io.IOException
import java.util.ArrayDeque

@RunWith(AndroidJUnit4::class)
class AlbumListUiTest {

    private var idlingRegistered = false

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

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
    fun displaysAlbumsLoadedFromApi() {
        RetrofitInstance.setApiForTesting(
            FakeVinilosApiService(
                albumResponses = ArrayDeque(listOf({ Response.success(sampleAlbums()) })),
                albumDetails = mapOf(1 to sampleAlbumDetail()),
            )
        )

        launchActivity()

        onView(withId(R.id.albumsRecyclerView)).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.albumTitleText), withText("Kind of Blue"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.albumArtistText), withText("Miles Davis"))).check(matches(isDisplayed()))
        onView(allOf(withId(R.id.albumMetaText), withText("1959 · COLUMBIA"))).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun retriesLoadWhenRefreshIsTappedAfterAnError() {
        RetrofitInstance.setApiForTesting(
            FakeVinilosApiService(
                albumResponses = ArrayDeque(
                    listOf<() -> Response<List<Album>>>(
                        { throw IOException("Simulated network failure") },
                        { Response.success(sampleAlbums()) },
                    )
                ),
                albumDetails = mapOf(1 to sampleAlbumDetail()),
            )
        )

        launchActivity()

        onView(withId(R.id.errorText)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).perform(click())
        onView(allOf(withId(R.id.albumTitleText), withText("Kind of Blue"))).check(matches(isDisplayed()))
        onView(withId(R.id.errorText)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun opensAlbumDetailWhenAnAlbumIsTapped() {
        RetrofitInstance.setApiForTesting(
            FakeVinilosApiService(
                albumResponses = ArrayDeque(listOf({ Response.success(sampleAlbums()) })),
                albumDetails = mapOf(1 to sampleAlbumDetail()),
            )
        )

        launchActivity()

        onView(withId(R.id.albumsRecyclerView)).perform(clickRecyclerViewItemAt(0))

        onView(withId(R.id.tvName)).check(matches(withText("Kind of Blue")))
        onView(withId(R.id.tvArtist)).check(matches(withText("Miles Davis")))
        onView(withId(R.id.tvRecordLabel)).check(matches(withText("Columbia")))
    }

    private fun launchActivity() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activityRule.launchActivity(intent)
    }

    private fun clickRecyclerViewItemAt(position: Int): ViewAction = object : ViewAction {
        override fun getDescription(): String = "Click RecyclerView item at position $position"

        override fun getConstraints(): Matcher<View> = isAssignableFrom(RecyclerView::class.java)

        override fun perform(uiController: UiController, view: View) {
            val recyclerView = view as RecyclerView
            recyclerView.scrollToPosition(position)
            uiController.loopMainThreadUntilIdle()

            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                ?: throw PerformException.Builder()
                    .withCause(IllegalStateException("No item found at position $position"))
                    .build()

            if (!viewHolder.itemView.performClick()) {
                fail("The RecyclerView item at position $position could not be clicked")
            }

            uiController.loopMainThreadUntilIdle()
        }
    }

    private class FakeVinilosApiService(
        private val albumResponses: ArrayDeque<() -> Response<List<Album>>>,
        private val albumDetails: Map<Int, Album>,
    ) : VinilosApiService {
        override suspend fun getAlbums(): Response<List<Album>> {
            if (albumResponses.isEmpty()) {
                return Response.success(emptyList())
            }
            val nextResponse = albumResponses.removeFirst()
            return nextResponse.invoke()
        }

        override suspend fun getAlbum(id: Int): Response<Album> {
            val album = albumDetails[id] ?: throw IOException("Missing fake detail for album $id")
            return Response.success(album)
        }
    }

    companion object {
        private fun sampleAlbums(): List<Album> = listOf(
            sampleAlbumDetail(),
            Album(
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
        )

        private fun sampleAlbumDetail(): Album = Album(
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
                    id = 1000,
                    description = "Classic.",
                    rating = 5,
                )
            ),
            releaseDate = "1959-08-17T00:00:00.000Z",
            description = "Modal jazz landmark.",
            genre = "Jazz",
            recordLabel = "Columbia",
        )
    }
}
