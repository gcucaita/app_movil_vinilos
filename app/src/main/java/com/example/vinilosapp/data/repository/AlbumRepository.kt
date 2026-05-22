package com.example.vinilosapp.data.repository

import android.util.Log
import com.example.vinilosapp.data.cache.CacheManager
import com.example.vinilosapp.data.network.VinilosApiService
import com.example.vinilosapp.data.network.request.CreateAlbumRequest
import com.example.vinilosapp.data.serviceadapter.AlbumServiceAdapter
import com.example.vinilosapp.domain.model.Album
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
        CacheManager.getAlbumsList()?.let { cachedAlbums ->
            logDebug("Using cached albums data")
            return@withContext cachedAlbums
        }

        logDebug("Fetching albums data from API")
        incrementIdlingResource()

        var lastError: Exception? = null

        try {
            repeat(3) { attempt ->
                try {
                    val response = albumServiceAdapter.getAlbums()
                    if (response.isSuccessful) {
                        val albums = response.body()
                        logDebug("Data received: $albums")
                        albums?.let { CacheManager.putAlbumsList(it) }
                        return@withContext albums
                    } else {
                        logError("API Error Response: ${response.errorBody()?.string()}")
                    }
                } catch (e: Exception) {
                    lastError = e
                    logError("Attempt ${attempt + 1} failed: ${e.message}", e)
                    if (attempt < 2) delay(2000)
                }
            }
        } finally {
            decrementIdlingResource()
        }

        logError("All attempts failed: ${lastError?.message}", lastError)
        null
    }

    suspend fun getAlbum(id: Int): Album? = withContext(ioDispatcher) {
        incrementIdlingResource()
        try {
            val response = albumServiceAdapter.getAlbum(id)
            if (response.isSuccessful) {
                val album = response.body()
                logDebug("Album received: $album")
                album
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

    suspend fun createAlbum(request: CreateAlbumRequest): Result<Album> = withContext(ioDispatcher) {
        incrementIdlingResource()
        try {
            val response = albumServiceAdapter.createAlbum(request)
            if (response.isSuccessful) {
                val album = response.body()
                if (album != null) {
                    CacheManager.invalidateAlbumsListCache()
                    logDebug("Album created: $album")
                    Result.success(album)
                } else {
                    Result.failure(IllegalStateException("La respuesta del servidor no incluyo el album creado"))
                }
            } else {
                val errorMessage = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                    ?: "No se pudo crear el album"
                logError("API Error Response: $errorMessage")
                Result.failure(IllegalStateException(errorMessage))
            }
        } catch (e: Exception) {
            logError("Network Exception: ${e.message}", e)
            Result.failure(e)
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
