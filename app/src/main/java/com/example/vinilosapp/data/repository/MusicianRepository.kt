package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.serviceadapter.MusicianServiceAdapter
import com.example.vinilosapp.domain.model.Performer
import com.example.vinilosapp.helpers.EspressoIdlingResource
import kotlinx.coroutines.delay

class MusicianRepository(
    private val musicianServiceAdapter: MusicianServiceAdapter = MusicianServiceAdapter()
) {
    constructor(apiService: VinilosApiService) : this(MusicianServiceAdapter(apiService))

    suspend fun getAllMusicians(): List<Performer>? {
        CacheManager.getMusiciansList()?.let { cachedMusicians ->
            logDebug("Using cached musicians data")
            return cachedMusicians
        }

        logDebug("Fetching musicians data from API")
        incrementIdlingResource()

        var lastError: Exception? = null

        repeat(3) { attempt ->
            try {
                val response = musicianServiceAdapter.getMusicians()
                if (response.isSuccessful) {
                    val musicians = response.body()
                    logDebug("Data received: $musicians")
                    musicians?.let { CacheManager.putMusiciansList(it) }
                    decrementIdlingResource()
                    return musicians
                } else {
                    logError("API Error Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                lastError = e
                logError("Attempt ${attempt + 1} failed: ${e.message}", e)
                if (attempt < 2) delay(2000)
            }
        }

        decrementIdlingResource()
        logError("All attempts failed: ${lastError?.message}", lastError)
        return null
    }

    private fun incrementIdlingResource() {
        runCatching { EspressoIdlingResource.increment() }
    }

    private fun decrementIdlingResource() {
        runCatching { EspressoIdlingResource.decrement() }
    }

    private fun logDebug(message: String) {
        runCatching { Log.d("MusicianRepository", message) }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        runCatching { Log.e("MusicianRepository", message, throwable) }
    }
}
