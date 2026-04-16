package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.model.Album
import com.example.vinilosapp.data.network.RetrofitInstance
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.testing.EspressoIdlingResource

class AlbumRepository(
    private val apiService: VinilosApiService = RetrofitInstance.api
) {

    suspend fun getAllAlbums(): List<Album>? {
        CacheManager.getAlbumsList()?.let { cachedAlbums ->
            logDebug("Using cached albums data")
            return cachedAlbums
        }

        logDebug("Fetching albums data from API")
        incrementIdlingResource()

        return try {
            val response = apiService.getAlbums()
            if (response.isSuccessful) {
                val albums = response.body()
                logDebug("Data received: $albums")
                albums?.let { CacheManager.putAlbumsList(it) }
                albums
            } else {
                logError("API Error Response: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            logError("Network Exception: ${e.message}", e)
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
