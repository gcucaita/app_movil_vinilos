package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.network.request.CreateTrackRequest
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.domain.model.Track
import com.example.vinilosapp.helpers.EspressoIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AlbumRepository(
    private val albumServiceAdapter: AlbumServiceAdapter = AlbumServiceAdapter(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    constructor(apiService: VinilosApiService) : this(AlbumServiceAdapter(apiService))

    suspend fun getAllAlbums(): List<Album>? = withContext(ioDispatcher) {
        CacheManager.getAlbumsList()?.let { return@withContext it }

        incrementIdlingResource()

        try {
            repeat(3) { attempt ->
                try {
                    val response = albumServiceAdapter.getAlbums()
                    if (response.isSuccessful) {
                        val albums = response.body()
                        albums?.let { CacheManager.putAlbumsList(it) }
                        return@withContext albums
                    }
                } catch (e: Exception) {
                    if (attempt < 2) delay(2000)
                }
            }
        } finally {
            decrementIdlingResource()
        }

        null
    }

    suspend fun getAlbum(id: Int): Album? = withContext(ioDispatcher) {
        incrementIdlingResource()
        try {
            val response = albumServiceAdapter.getAlbum(id)
            return@withContext if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        } finally {
            decrementIdlingResource()
        }
    }

    // ✅ SOLO UNA versión (IMPORTANTE)
    suspend fun createAlbum(request: CreateAlbumRequest): Album? = withContext(ioDispatcher) {
        incrementIdlingResource()
        try {
            val response = albumServiceAdapter.createAlbum(request)
            val album = response.body()

            if (response.isSuccessful && album != null) {
                CacheManager.invalidateAlbumsListCache()
                return@withContext album
            }
            null
        } finally {
            decrementIdlingResource()
        }
    }

    suspend fun addTrack(albumId: Int, request: CreateTrackRequest): Track? =
        withContext(ioDispatcher) {
            incrementIdlingResource()
            try {
                val response = albumServiceAdapter.addTrack(albumId, request)
                response.body()
            } finally {
                decrementIdlingResource()
            }
        }

    private fun incrementIdlingResource() {
        runCatching { EspressoIdlingResource.increment() }
    }

    private fun decrementIdlingResource() {
        runCatching { EspressoIdlingResource.decrement() }
    }

    private fun logError(message: String) {
        runCatching { Log.e("AlbumRepository", message) }
    }
    suspend fun addPerformerToAlbum(albumId: Int, performerId: Int): Album? = withContext(ioDispatcher) {
        incrementIdlingResource()
        try {
            val response = albumServiceAdapter.addPerformerToAlbum(albumId, performerId)
            if (response.isSuccessful) response.body() else null
        } finally {
            decrementIdlingResource()
        }
    }
}