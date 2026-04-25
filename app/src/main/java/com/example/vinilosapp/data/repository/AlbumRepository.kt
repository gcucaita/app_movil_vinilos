package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
import com.example.vinilosapp.helpers.EspressoIdlingResource

class AlbumRepository(
    private val albumServiceAdapter: AlbumServiceAdapter = AlbumServiceAdapter()
) {
    constructor(apiService: VinilosApiService) : this(AlbumServiceAdapter(apiService))

    suspend fun getAllAlbums(): List<Album>? {
        CacheManager.getAlbumsList()?.let { cachedAlbums ->
            logDebug("Using cached albums data")
            return cachedAlbums
        }

        logDebug("Fetching albums data from API")
        incrementIdlingResource()

        return try {
            val response = albumServiceAdapter.getAlbums()
            if (response.isSuccessful) {
                val albums = response.body()
                logDebug("Data received: $albums")
                albums?.let { CacheManager.putAlbumsList(it) }
                albums
            } else {
                logError("API Error Response: ${response.errorBody()?.string()}")
                null
            }
        } catch (exception: Exception) {
            logError("Network Exception: ${exception.message}", exception)
            null
        } finally {
            decrementIdlingResource()
        }
    }
    suspend fun getAlbum(id: Int): Album? {
        incrementIdlingResource()
        return try {
            val response = albumServiceAdapter.getAlbum(id)
            if (response.isSuccessful) {
                val album = response.body()
                logDebug("Album received: $album")
                album
            } else {
                logError("API Error Response: ${response.errorBody()?.string()}")
                null
            }
        } catch (exception: Exception) {
            logError("Network Exception: ${exception.message}", exception)
            null
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

    private fun logDebug(message: String) {
        runCatching { Log.d("AlbumRepository", message) }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        runCatching { Log.e("AlbumRepository", message, throwable) }
    }
}
