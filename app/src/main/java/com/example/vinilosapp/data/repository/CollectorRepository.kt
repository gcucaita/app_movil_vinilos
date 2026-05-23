package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.serviceadapter.CollectorServiceAdapter
import com.example.vinilosapp.domain.model.Collector
import com.example.vinilosapp.helpers.EspressoIdlingResource
import kotlinx.coroutines.delay

class CollectorRepository(
    private val collectorServiceAdapter: CollectorServiceAdapter = CollectorServiceAdapter()
) {
    constructor(apiService: VinilosApiService) : this(CollectorServiceAdapter(apiService))

    suspend fun getAllCollectors(): List<Collector>? {
        CacheManager.getCollectorsList()?.let { cachedCollectors ->
            logDebug("Using cached collectors data")
            return cachedCollectors
        }

        logDebug("Fetching collectors data from API")
        incrementIdlingResource()

        var lastError: Exception? = null

        repeat(3) { attempt ->
            try {
                val response = collectorServiceAdapter.getCollectors()
                if (response.isSuccessful) {
                    val collectors = response.body()
                    logDebug("Data received: $collectors")
                    collectors?.let { CacheManager.putCollectorsList(it) }
                    decrementIdlingResource()
                    return collectors
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

    suspend fun getCollector(id: Int): Collector? {
        incrementIdlingResource()
        return try {
            val response = collectorServiceAdapter.getCollector(id)
            if (response.isSuccessful) {
                val collector = response.body()
                logDebug("Collector received: $collector")
                collector
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
        runCatching { Log.d("CollectorRepository", message) }
    }

    private fun logError(message: String, throwable: Throwable? = null) {
        runCatching { Log.e("CollectorRepository", message, throwable) }
    }
}